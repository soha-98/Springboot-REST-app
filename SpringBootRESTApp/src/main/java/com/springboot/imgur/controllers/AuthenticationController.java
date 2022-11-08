package com.springboot.imgur.controllers;

import java.util.Base64;

import java.util.HashSet;
import java.util.Set;
import javax.accessibility.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.imgur.jwt.security.JwtProvider;
import com.springboot.imgur.repository.RoleRepository;
import com.springboot.imgur.repository.UserRepository;
import com.springboot.imgur.responsedata.Imgur;
import com.springboot.imgur.responsedata.JsonResponse;
import com.springboot.imgur.responsedata.JwtResponse;
import com.springboot.imgur.responsedata.LoginForm;
import com.springboot.imgur.responsedata.Role;
import com.springboot.imgur.responsedata.RoleName;
import com.springboot.imgur.responsedata.SignUpForm;
import com.springboot.imgur.responsedata.User;


@CrossOrigin(origins = "*", maxAge = 45600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtProvider jwtProvider;
    
    @Value("${app.imgurAccessToken}")
    private String imgurAccessToken;
    
    private String baseUrl = "https://api.imgur.com/";
	private HttpHeaders headers;
	
	 @PostMapping("/signin")
	    public JwtResponse authenticateUser(@Validated @RequestBody LoginForm loginRequest) {
	    	String jwt="";
	    	User user=null;
	    	JwtResponse jwtResponse= new JwtResponse();
	    	if(loginRequest.getUsername()==null || loginRequest.getPassword()==null) {
	    		jwtResponse.setStatus(401);
	    		jwtResponse.setMessage("username and Password both required");

	    	}
	    	
	    	try {
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        loginRequest.getUsername(),
	                        loginRequest.getPassword()
	                )
	        );
	        
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	          jwt = jwtProvider.generateJwtToken(authentication);        
	          user=userRepository.findUsername(loginRequest.getUsername());
	        user.setPassword("");

	    	}catch (BadCredentialsException e) {
				jwtResponse.setStatus(401);
	    		jwtResponse.setMessage(e.getMessage());
			}catch (Exception e) {
				jwtResponse.setStatus(401);
	    		jwtResponse.setMessage(e.getMessage());
			}
	    	jwtResponse.setAccessToken(jwt);
	    	jwtResponse.setUserData(user);
	      	return jwtResponse;
	    }
	 
	 @PostMapping("/signup")
	    public ResponseEntity<String> registerUser(@Validated @RequestBody SignUpForm signUpRequest) {
	      try {   	
	    	  if(signUpRequest.getUsername()==null) { 
	    		  return new ResponseEntity<String>("Fail -> Username is required!",
	                      HttpStatus.BAD_REQUEST);
	    	  }
	    	  if(signUpRequest.getPassword()==null) { 
	    		  return new ResponseEntity<String>("Fail -> Password is required!",
	                      HttpStatus.BAD_REQUEST);
	    	  }
	    	  if(signUpRequest.getEmail()==null) { 
	    		  return new ResponseEntity<String>("Fail -> Email is required!",
	                      HttpStatus.BAD_REQUEST);
	    	  }
	    	if(userRepository.existsByUsername(signUpRequest.getUsername())) {
	            return new ResponseEntity<String>("Fail -> Username is already taken!",
	                    HttpStatus.BAD_REQUEST);
	        }

	        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
	            return new ResponseEntity<String>("Fail -> Email is already in use!",
	                    HttpStatus.BAD_REQUEST);
	        }

	        // Creating user's account
	        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
	                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

	        Set<String> strRoles = signUpRequest.getRole();
	        Set<Role> roles = new HashSet<>();

	        strRoles.forEach(role -> {
	        	RoleName roleStr=role.equalsIgnoreCase("admin")?RoleName.ROLE_ADMIN:role.equalsIgnoreCase("pm")?RoleName.ROLE_PM:RoleName.ROLE_USER;
	 			Role adminRole = roleRepository.findByName(roleStr)
		                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
		    			roles.add(adminRole);	    			        	
	        });
	        
	        user.setRoles(roles);
	        userRepository.save(user);
	        return ResponseEntity.ok().body("User registered successfully!");
	      }catch (Exception e) {    	
	    	  return ResponseEntity.ok().body(e.getMessage());
	      }
	    }
	 
	 @PostMapping("/image/delete")
	    public JsonResponse deleteFiletoImgur(@Validated @RequestBody LoginForm loginRequest) {
			if(loginRequest.getUsername()==null) { 
		          return new JsonResponse(null,"Fail -> Username is required!","uploaded.failure",false);
				}
		  	  if(loginRequest.getPassword()==null) { 
		          return new JsonResponse(null,"Fail -> Password is required!","uploaded.failure",false);
		  	  }
		  	  
		  	  if(loginRequest.getImageId()==null) { 
		     	 return new JsonResponse(null,"Fail -> ImageId is required!","uploaded.failure",false);
		  	  }

	        
	        try {
	            Authentication authentication = authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(
	                            loginRequest.getUsername(),
	                            loginRequest.getPassword()
	                    )
	            );                
	            User user=userRepository.findUsername(loginRequest.getUsername());
	        	
	        	Imgur obj=deletetoImgur("3/image/"+loginRequest.getImageId());
	            user.setImgurId(null);
	            user.setImgurLink(null);            
	            userRepository.save(user);
	            user.setPassword("");
	            return new JsonResponse(user,"delete success","delete.success",true);                   	
	        }catch(Exception e) {
	            return new JsonResponse(null,e.getMessage(),"delete.failure",false);
	        }
	    }
	 
	 
	 @PostMapping(path = "/image/upload", consumes = "multipart/form-data", produces = "application/json")
	    public JsonResponse uploadFiletoImgur(@RequestParam("username") String username,@RequestParam("password") String password,
	                                  @RequestParam("file") MultipartFile file) {
			
			if(username==null) { 
	          return new JsonResponse(null,"Fail -> Username is required!","uploaded.failure",false);
			}
	  	  if(password==null) { 
	          return new JsonResponse(null,"Fail -> Password is required!","uploaded.failure",false);
	  	  }
	  	  
	  	  if(file==null) { 
	     	 return new JsonResponse(null,"Fail -> file is required!","uploaded.failure",false);
	  	  }
			
	        
	        if (!file.isEmpty()) {
	            try {
	        		Authentication authentication = authenticationManager.authenticate(
	                        new UsernamePasswordAuthenticationToken(username, password)
	                );		
	                User user=userRepository.findUsername(username);	

	            	byte[] bytes = file.getBytes();                
	                String fileBase64 = Base64.getEncoder().encodeToString(bytes);                
	                JwtResponse jwtResponse=new JwtResponse();
	                jwtResponse.setImage(fileBase64);
	                JwtResponse img= uploadtoImgur("3/image",jwtResponse);
	                user.setImgurId(img.getData().getId());
	                user.setImgurLink(img.getData().getLink());
	                userRepository.save(user);
	    	        user.setPassword("");
	            return new JsonResponse(user,"uploaded success","uploaded.success",true);
	            } catch (Exception e) {
	                return new JsonResponse(null,e.getMessage(),"uploaded.failure",false);
	            }
	        } else {

	        	 return new JsonResponse(null,"Fail -> file is required!","uploaded.failure",false);
	        }
	    }
	 
	 
	    
	    @PostMapping("/image/view")
	    public JsonResponse viewFiletoImgur(@Validated @RequestBody LoginForm loginRequest) {
			if(loginRequest.getUsername()==null) { 
		          return new JsonResponse(null,"Fail -> Username is required!","uploaded.failure",false);
				}
		  	  if(loginRequest.getPassword()==null) { 
		          return new JsonResponse(null,"Fail -> Password is required!","uploaded.failure",false);
		  	  }

	                              
	        try {
	        	Authentication authentication = authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(
	                            loginRequest.getUsername(),
	                            loginRequest.getPassword()
	                    )
	            );  
	        	
	        	User user=userRepository.findUsername(loginRequest.getUsername());        	
	            user.setPassword("");
	            return new JsonResponse(user,"view success","delete.success",true);                   	
	        }catch(Exception e) {
	        	
	        }
	        return new JsonResponse(null,"view failure","delete.failure",false);                
	    }
		
		  public JwtResponse uploadtoImgur(String uri, JwtResponse json) {   
			  RestTemplate restTemplate =new RestTemplate();
			    this.headers = new HttpHeaders();
			    this.headers.add("Content-Type", "application/json");
			    this.headers.add("Accept", "*/*");
			    this.headers.add("Authorization", "Bearer "+imgurAccessToken);
			    // Data attached to the request.
			     HttpEntity<JwtResponse> requestBody = new HttpEntity<JwtResponse>(json, this.headers);
			    JwtResponse response=restTemplate.postForObject(baseUrl + uri, requestBody, JwtResponse.class);
		    return response;
		  }
		  
		  public Imgur deletetoImgur(String uri) {   
			  RestTemplate restTemplate =new RestTemplate();
			    this.headers = new HttpHeaders();
			    this.headers.add("Content-Type", "application/json");
			    this.headers.add("Accept", "*/*");
			    this.headers.add("Authorization", "Bearer "+imgurAccessToken);
			    // Data attached to the request.
			     HttpEntity<Imgur> requestBody = new HttpEntity<Imgur>(this.headers);
			     ResponseEntity<Imgur> response=restTemplate.exchange(baseUrl + uri, HttpMethod.DELETE, requestBody, Imgur.class);
		    return response.getBody();
		  }
		

	}