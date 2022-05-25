package com.partyluck.party_luck.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtTokenUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Service
public class NaverUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public NaverUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public String loginWithNaver(String code, String state, HttpServletResponse responseh) throws JsonProcessingException {



        System.out.println(code);
        System.out.println(state);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=XChO_shb0XLnJL7kCokg&client_secret=7mhLpvSE9G&code="+code+"&state=STATE_STRING",
                HttpMethod.GET,
                naverTokenRequest,
                String.class
        );
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String s= jsonNode.get("access_token").asText();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + s);
        HttpEntity<MultiValueMap<String, String>> naverInfoRequest =
                new HttpEntity<>(headers2);
        RestTemplate rt2 = new RestTemplate();
        ResponseEntity<String> response2 = rt2.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverInfoRequest,
                String.class
        );
        String responsebody2=response2.getBody();
        ObjectMapper objectMapper2=new ObjectMapper();
        JsonNode jsonNode2=objectMapper2.readTree(responsebody2);
        String email1=jsonNode2.get("response").get("email").asText();
        String nickname1=jsonNode2.get("response").get("nickname").asText();
        String name1=jsonNode2.get("response").get("name").asText();
        System.out.println(email1);
        System.out.println(name1);

        User naveruser=userRepository.findByEmail(email1).orElse(null);
        if(naveruser==null){
            naveruser=new User();
            naveruser.setNickname(name1);
            naveruser.setEmail(email1);
            naveruser.setUsername(email1);
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            naveruser.setPassword(encodedPassword);
            userRepository.save(naveruser);
        }
        UserDetails userDetails = new UserDetailsImpl(naveruser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails2=new UserDetailsImpl(naveruser);
        final String token = JwtTokenUtils.generateJwtToken(userDetails2);
        System.out.println(token);
        responseh.addHeader("Authorization", "BEARER" + " " + token);
        System.out.println(responseh.getStatus());
        return token;




    }
}
