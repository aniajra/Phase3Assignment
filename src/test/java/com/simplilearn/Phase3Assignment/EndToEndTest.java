package com.simplilearn.Phase3Assignment;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class EndToEndTest {

	private Response response;
	private static String empId;

	@BeforeMethod
	@BeforeTest
	public void setUp() {

		RestAssured.baseURI = "http://localhost:3000";

	}

	@Test
	public void endToEndTest() {

		// Step a
		response = getAllEmployees();

		validatingStatusCode(200);

		// Step b
		response = createEmployee("John", 8000);

		validatingStatusCode(201);

		// Step c
		empId = getCreatedEmpId();

		response = getSingleEmp(empId);

		validatingStatusCode(200);

		Assert.assertEquals(getEmployeeNameAsString(), "John");

		// Step d
		response = updateEmployee("Smith");

		validatingStatusCode(200);

		// Step e
		response = getSingleEmp(empId);

		validatingStatusCode(200);

		Assert.assertEquals(getEmployeeNameAsString(), "Smith");

		// Step f
		response = deleteEmployee(empId);

		validatingStatusCode(200);

		// Step g

		response = getSingleEmp(empId);

		validatingStatusCode(404);

		// Step h
		response = getAllEmployees();

		System.out.println(" Validating data does not contain the deleted employee");

		Assert.assertFalse(getEmployeeNameAsList().contains("Smith"));
		
		System.out.println("****End To End Test Completed****");

	}

	private void validatingStatusCode(int status) {

		System.out.println(" Validating status code " + status);

		Assert.assertEquals(status, response.getStatusCode());

	}

	// method to fetch all employees
	private Response getAllEmployees() {

		System.out.println("Get All Employees");

		return RestAssured.given().get("employees");

	}

	// method to create Employee using Map
	public Response createEmployee(String name, int salary) {

		System.out.println("Creating new Employee ");

		Map<String, Object> mapObj = new HashMap<String, Object>();
		mapObj.put("name", name);
		mapObj.put("salary", salary);

		return RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON).body(mapObj)
				.post("employees/create");

	}

	// method to fetch single employee
	public Response getSingleEmp(String empId) {

		System.out.println("Get Single Employee with ID " + empId);

		return RestAssured.given().get("employees/{id}", empId);

	}

	// method to get the created employee ID
	private String getCreatedEmpId() {

		JsonPath jpath = response.jsonPath();

		String id = jpath.get("id").toString();

		System.out.println(" Employee with Id : " + id + " created Successfully");

		System.out.println(response.getBody().asString());

		return id;

	}

	// method to get the created Employee Name
	private String getEmployeeNameAsString() {

		// Getting Via Json Path
		JsonPath jpath = response.jsonPath();

		String name = jpath.get("name");

		System.out.println(" Name of Employee to Verify : " + name);

		return name;
	}

	// method to get the created Employee Name
	private List<String> getEmployeeNameAsList() {

		// Getting Via Json Path
		JsonPath jpath = response.jsonPath();

		List<String> names = jpath.get("name");

		return names;
	}

	// method to update the employee name
	private Response updateEmployee(String name) {

		System.out.println("Updating the employee name with ID " + empId);

		Map<String, Object> mapObj = new HashMap<String, Object>();
		mapObj.put("name", name);
		mapObj.put("salary", 8000);

		return RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON).body(mapObj)
				.put("employees/{id}", empId);
	}

	// method to delete the created employee
	private Response deleteEmployee(String empId) {

		System.out.println("Deleting the employee with ID " + empId);

		return RestAssured.given().delete("employees/{id}", empId);
	}

}