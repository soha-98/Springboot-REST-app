package com.springboot.imgur.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.springboot.imgur.responsedata.JsonResponse;
import com.springboot.imgur.responsedata.JwtResponse;
import com.springboot.imgur.responsedata.LoginForm;
import com.springboot.imgur.responsedata.SignUpForm;
import com.springboot.imgur.controllers.AuthenticationController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AuthenticationControllerTest {
	
	
	@Autowired
	private AuthenticationController authRestAPIs;	
	
	@Test
	public void registerUserTestCase() throws Exception {
		SignUpForm signUpForm=new SignUpForm();
		signUpForm.setUsername("suresh12");
		signUpForm.setPassword("123451");	
		signUpForm.setName("suresh1");
		signUpForm.setEmail("info123@gmail.com");
		HashSet<String> set = new HashSet<String>();
		set.add("user");				
		signUpForm.setRole(set);
		ResponseEntity<String> jwtResponse=authRestAPIs.registerUser(signUpForm);					
		assertThat(jwtResponse.getBody(),is("User registered successfully!"));		
	}
	
	@Test
	public void registerUserTestCaseDuplicateUsername() throws Exception {
		SignUpForm signUpForm=new SignUpForm();
		signUpForm.setUsername("suresh10");
		signUpForm.setPassword("123451");	
		signUpForm.setName("suresh1");
		signUpForm.setEmail("info10@gmail.com");
		HashSet<String> set = new HashSet<String>();
		set.add("user");				
		signUpForm.setRole(set);
		ResponseEntity<String> jwtResponse=authRestAPIs.registerUser(signUpForm);	
		ResponseEntity<String> jwtResponse1=authRestAPIs.registerUser(signUpForm);							
		assertThat(jwtResponse1.getBody(),is("Fail -> Username is already taken!"));		
	}
	
	@Test
	public void registerUserTestCaseDuplicateEmail() throws Exception {
		SignUpForm signUpForm=new SignUpForm();
		signUpForm.setUsername("suresh11");
		signUpForm.setPassword("123451");	
		signUpForm.setName("suresh1");
		signUpForm.setEmail("info10@gmail.com");
		HashSet<String> set = new HashSet<String>();
		set.add("user");				
		signUpForm.setRole(set);
		ResponseEntity<String> jwtResponse=authRestAPIs.registerUser(signUpForm);	
		ResponseEntity<String> jwtResponse1=authRestAPIs.registerUser(signUpForm);		
		assertThat(jwtResponse1.getBody(),is("Fail -> Email is already in use!"));		
	}
	
	@Test
	public void registerUserTestCaseUsernameNull() throws Exception {
		SignUpForm signUpForm=new SignUpForm();
		signUpForm.setPassword("123451");	
		signUpForm.setName("suresh1");
		signUpForm.setEmail("info11@gmail.com");
		HashSet<String> set = new HashSet<String>();
		set.add("user");				
		signUpForm.setRole(set);
		ResponseEntity<String> jwtResponse=authRestAPIs.registerUser(signUpForm);	
		System.out.println("jwtResponse.getBody()===>"+jwtResponse.getBody());
		assertThat(jwtResponse.getBody(),is("Fail -> Username is required!"));		
	}
	
	@Test
	public void registerUserTestCasePasswordNull() throws Exception {
		SignUpForm signUpForm=new SignUpForm();
		signUpForm.setUsername("suresh11");	
		signUpForm.setName("suresh1");
		signUpForm.setEmail("info11@gmail.com");
		HashSet<String> set = new HashSet<String>();
		set.add("user");				
		signUpForm.setRole(set);
		ResponseEntity<String> jwtResponse=authRestAPIs.registerUser(signUpForm);	
		System.out.println("jwtResponse.getBody()===>"+jwtResponse.getBody());
		assertThat(jwtResponse.getBody(),is("Fail -> Password is required!"));		
	}
	
	@Test
	public void registerUserTestCaseEmailNull() throws Exception {
		SignUpForm signUpForm=new SignUpForm();
		signUpForm.setUsername("suresh11");	
		signUpForm.setPassword("123451");	
		signUpForm.setName("suresh1");
		HashSet<String> set = new HashSet<String>();
		set.add("user");				
		signUpForm.setRole(set);
		ResponseEntity<String> jwtResponse=authRestAPIs.registerUser(signUpForm);	
		System.out.println("jwtResponse.getBody()===>"+jwtResponse.getBody());
		assertThat(jwtResponse.getBody(),is("Fail -> Email is required!"));		
	}

	@Test
	@Sql("user.sql")
	public void authenticateUserTestCase_success() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		loginRequest.setPassword("123456");		
		JwtResponse jwtResponse=authRestAPIs.authenticateUser(loginRequest);		
		assertThat(jwtResponse.getAccessToken()).isNotNull();		
	}
	
	@Test
	public void authenticateUserTestCasePasswordNull() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		//loginRequest.setPassword("123451");		
		JwtResponse jwtResponse=authRestAPIs.authenticateUser(loginRequest);		
		assertThat(jwtResponse.getStatus(), is(401));		
	}
	
	@Test
	public void authenticateUserTestCaseUsernameNull() throws Exception {
		LoginForm loginRequest=new LoginForm();
		//loginRequest.setUsername("suresh");
		loginRequest.setPassword("123451");		
		JwtResponse jwtResponse=authRestAPIs.authenticateUser(loginRequest);		
		assertThat(jwtResponse.getStatus(), is(401));		
	}
	
	@Test
	public void upload_sucess() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/java/com/assign/test/ImageforTest.png");
	    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);		
		JsonResponse jsonResponse=authRestAPIs.uploadFiletoImgur("suresh", "123456",multipartFile);		
		assertThat(jsonResponse.isSuccess(), is(true));		
	}
	
	@Test
	public void uploadMissingUsername() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/java/com/assign/test/ImageforTest.png");
	    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);		
		JsonResponse jsonResponse=authRestAPIs.uploadFiletoImgur(null, "123456",multipartFile);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void uploadMissingPassword() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/java/com/assign/test/ImageforTest.png");
	    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);		
		JsonResponse jsonResponse=authRestAPIs.uploadFiletoImgur("suresh", null,multipartFile);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void uploadMissingFile() throws Exception {
		JsonResponse jsonResponse=authRestAPIs.uploadFiletoImgur("suresh", "123456",null);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void uploadBadCredentials() throws Exception {
		FileInputStream fis = new FileInputStream("src/test/java/com/assign/test/ImageforTest.png");
	    MockMultipartFile multipartFile = new MockMultipartFile("file", fis);		
		JsonResponse jsonResponse=authRestAPIs.uploadFiletoImgur("suresh", "1234561",multipartFile);		
		assertThat(jsonResponse.isSuccess(), is(false));
		assertThat(jsonResponse.getMessage(), is("Bad credentials"));		
	}
	
	@Test
	public void delete_file_sucess() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		loginRequest.setPassword("123456");		
		loginRequest.setImageId("i6GAvYv");
		
		JsonResponse jsonResponse=authRestAPIs.deleteFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(true));		
	}
	
	@Test
	public void deleteFileMissingUsername() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setPassword("123456");		
		loginRequest.setImageId("i6GAvYv");		
		JsonResponse jsonResponse=authRestAPIs.deleteFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void deleteFileMissingPassword() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		loginRequest.setImageId("hrGGYA5");		
		JsonResponse jsonResponse=authRestAPIs.deleteFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void deleteFileMissingImagesId() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		loginRequest.setPassword("123456");		
		
		JsonResponse jsonResponse=authRestAPIs.deleteFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void ImageViewSucess() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		loginRequest.setPassword("123456");				
		JsonResponse jsonResponse=authRestAPIs.viewFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(true));		
	}
	
	@Test
	public void ImageViewMissingUsername() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setPassword("123456");				
		JsonResponse jsonResponse=authRestAPIs.viewFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}
	
	@Test
	public void ImageViewMissingPassword() throws Exception {
		LoginForm loginRequest=new LoginForm();
		loginRequest.setUsername("suresh");
		JsonResponse jsonResponse=authRestAPIs.viewFiletoImgur(loginRequest);		
		assertThat(jsonResponse.isSuccess(), is(false));		
	}

	
	
}