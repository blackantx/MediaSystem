package hs.mediasystem.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AnnotedRecordMapperTest {
  @Mock private Database.Transaction transaction;
  @Mock private Database database;

  private AnnotatedRecordMapper<TestEmployee> employeeMapper;
  private AnnotatedRecordMapper<TestCar> carMapper;
  private AnnotatedRecordMapper<TestOption> optionMapper;

  private TestEmployee testEmployee;
  private TestEmployer testEmployer;
  private TestCar testCar;
  private TestOption testOption;

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);

    employeeMapper = AnnotatedRecordMapper.create(TestEmployee.class);
    carMapper = AnnotatedRecordMapper.create(TestCar.class);
    optionMapper = AnnotatedRecordMapper.create(TestOption.class);

    testEmployee = new TestEmployee();
    testEmployer = new TestEmployer();
    testCar = new TestCar();
    testOption = new TestOption();

    when(transaction.getDatabase()).thenReturn(database);
    when(database.beginTransaction()).thenReturn(transaction);
    when(database.beginReadOnlyTransaction()).thenReturn(transaction);
  }

  @Test
  public void shouldExtractIDsAndValues() {
    TestCar car = new TestCar();

    car.setId(new TestPlate("DE", "012345"));
    car.setBrand("V");
    car.setModel("5");

    TestOption testOption = new TestOption();

    testOption.setCar(car);
    testOption.setId(2);
    testOption.setOptionDescription("Dice");

    Map<String, Object> values = optionMapper.extractValues(testOption);

    assertEquals("Dice", values.get("optiondescription"));
    assertEquals("DE", values.get("car_cc"));
    assertEquals("012345", values.get("car_plate"));
    assertEquals(3, values.size());

    Map<String, Object> ids = optionMapper.extractIds(testOption);

    assertEquals(new Integer(2), ids.get("id"));
    assertEquals(1, ids.size());

    Map<String, Object> carIDs = carMapper.extractIds(car);

    assertEquals("DE", carIDs.get("countrycode"));
    assertEquals("012345", carIDs.get("licenseplate"));
  }

  @Test
  public void shouldApplyRelationUsingStub() {
    employeeMapper.applyValues(transaction, testEmployee, new HashMap<String, Object>() {{
      put("id", 2);
      put("fired", true);
      put("name", "John Doe");
      put("employers_id", 101);
    }});

    assertEquals(new Integer(2), testEmployee.getId());
    assertTrue(testEmployee.isFired());
    assertEquals("John Doe", testEmployee.getName());
    assertEquals(TestEmployer.class, testEmployee.getEmployer().getClass());
  }

  @Test
  public void shouldApplyRelationUsingAssociatedObject() {
    testEmployer.setId(102);

    when(transaction.findAssociatedObject(TestEmployer.class, new Object[] {102})).thenReturn(testEmployer);

    employeeMapper.applyValues(transaction, testEmployee, new HashMap<String, Object>() {{
      put("id", 2);
      put("fired", false);
      put("name", "John Doe");
      put("employers_id", 102);
    }});

    assertEquals(new Integer(2), testEmployee.getId());
    assertFalse(testEmployee.isFired());
    assertEquals("John Doe", testEmployee.getName());
    assertEquals(testEmployer, testEmployee.getEmployer());
  }

  @Test
  public void shouldApplyMultiFieldId() {
    carMapper.applyValues(transaction, testCar, new HashMap<String, Object>() {{
      put("countrycode", "NL");
      put("licenseplate", "01-AA-01");
      put("brand", "Australian Martini");
      put("model", "1");
    }});

    assertEquals(new TestPlate("NL", "01-AA-01"), testCar.getId());
    assertEquals("Australian Martini", testCar.getBrand());
    assertEquals("1", testCar.getModel());
  }

  @Test
  public void shouldApplyMultiFieldRelation() {
    testCar.setId(new TestPlate("NL", "01-AA-02"));

    when(transaction.findAssociatedObject(TestCar.class, new Object[] {"NL", "01-AA-02"})).thenReturn(testCar);

    optionMapper.applyValues(transaction, testOption, new HashMap<String, Object>() {{
      put("id", 1);
      put("car_cc", "NL");
      put("car_plate", "01-AA-02");
      put("optiondescription", "Mirror Dice");
    }});

    assertEquals(testCar, testOption.getCar());
    assertEquals(new Integer(1), testOption.getId());
    assertEquals("Mirror Dice", testOption.getOptionDescription());
  }

  @Test
  public void shouldFetchStub() {
    when(transaction.selectUnique(TestEmployee.class, "id=?", 201)).thenReturn(testEmployee);

    carMapper.applyValues(transaction, testCar, new HashMap<String, Object>() {{
      put("countrycode", "NL");
      put("licenseplate", "01-AA-01");
      put("brand", "Australian Martini");
      put("model", "1");
      put("owners_id", 201);
    }});

    assertEquals(new TestPlate("NL", "01-AA-01"), testCar.getId());
    assertEquals("Australian Martini", testCar.getBrand());
    assertEquals("1", testCar.getModel());
    assertEquals(testEmployee, testCar.getOwner());
  }

  @Test
  public void shouldFetch() {
    when(transaction.select(TestEmployee.class, "employers_id=?", 500)).thenReturn(new ArrayList<TestEmployee>() {{
      add(new TestEmployee("Anna"));
      add(new TestEmployee("Bea"));
    }});

    TestEmployer employer = new TestEmployer();

    employer.setDatabase(database);
    employer.setId(500);

    List<TestEmployee> results = AnnotatedRecordMapper.fetch(TestEmployee.class, employer);

    assertEquals(2, results.size());
    assertEquals("Anna", results.get(0).getName());
    assertEquals("Bea", results.get(1).getName());
  }

  @Test
  public void shouldReturnNoResultsWhenCallingFetchWithParentThatHasNoId() {
    TestEmployer employer = new TestEmployer();

    List<TestEmployee> results = AnnotatedRecordMapper.fetch(TestEmployee.class, employer);

    assertEquals(0, results.size());
  }

  @Test(expected = MappingException.class)
  public void shouldRejectClassWithDuplicateColumnSpecification() {
    @Table(name = "bad")
    class TestBadObjectWithDuplicateColumnSpecification extends DatabaseObject {
      @Column
      private int field;

      private String someOtherField;

      @Column
      public String getField() {
        return someOtherField;
      }

      @SuppressWarnings("unused")
      public void setField(String field) {
        this.someOtherField = field;
      }
    }

    AnnotatedRecordMapper.create(TestBadObjectWithDuplicateColumnSpecification.class);
  }

  @Test(expected = MappingException.class)
  public void shouldRejectClassWithMissingTableAnnotation() {
    class TestBadObjectWithMissingTableAnnotation extends DatabaseObject {
      @Column
      private int field;
    }

    AnnotatedRecordMapper.create(TestBadObjectWithMissingTableAnnotation.class);
  }

  @Test(expected = MappingException.class)
  public void shouldRejectClassWithMissingSetters() {
    @Table(name = "bad")
    class TestBadObjectWithMissingSetters extends DatabaseObject {
      @Column
      public int getField() {
        return 0;
      }
    }

    AnnotatedRecordMapper.create(TestBadObjectWithMissingSetters.class);
  }
}
