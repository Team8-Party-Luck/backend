package com.partyluck.party_luck.security.provider;


import com.partyluck.party_luck.domain.User;
import com.partyluck.party_luck.repository.UserRepository;
import com.partyluck.party_luck.security.UserDetailsImpl;
import com.partyluck.party_luck.security.jwt.JwtDecoder;
import com.partyluck.party_luck.security.jwt.JwtPreProcessingToken;
import com.partyluck.party_luck.security.jwt.JwtTokenUtils;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthProvider implements AuthenticationProvider {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        String username = jwtDecoder.decodeUsername(token);

        // TODO: API 사용시마다 매번 User DB 조회 필요
        //  -> 해결을 위해서는 UserDetailsImpl 에 User 객체를 저장하지 않도록 수정
        //  ex) UserDetailsImpl 에 userId, username, role 만 저장
        //    -> JWT 에 userId, username, role 정보를 암호화/복호화하여 사용
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find " + username));;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtPreProcessingToken.class.isAssignableFrom(authentication);
    }


    /**
     * 웹소켓 통신할 때
     * Jwt Token의 유효성을 체크 로직.
     */

    // Base64 decoding Error가 나는데, JWT_SECRET의 문자열에 특수문자 '-' 등에서 나는 오류 같아서
    // .getBytes()로 바꾸어 주었다.
//    public boolean validateToken(String jwt) {
//        return this.getClaims(jwt) != null;
//    }
//
//    private Jws<Claims> getClaims(String jwt) {
//        try {
//            return Jwts.parserBuilder().setSigningKey(JwtTokenUtils.JWT_SECRET).build().parseClaimsJws(jwt);
//        } catch (SignatureException ex) {
//            log.error("Invalid JWT signature");
//            throw ex;
//        } catch (MalformedJwtException ex) {
//            log.error("Invalid JWT token");
//            throw ex;
//        } catch (ExpiredJwtException ex) {
//            log.error("Expired JWT token");
//            throw ex;
//        } catch (UnsupportedJwtException ex) {
//            log.error("Unsupported JWT token");
//            throw ex;
//        } catch (IllegalArgumentException ex) {
//            log.error("JWT claims string is empty.");
//            throw ex;
//        }
//    }
//    대안 2)
    public boolean validateToken(String authToken) throws JwtException{

        try {
            System.out.println("Secret Key : " + JwtTokenUtils.JWT_SECRET);
            System.out.println("JwtTokenUtils.JWT_SECRET.getBytes : " + Arrays.toString(JwtTokenUtils.JWT_SECRET.getBytes()));
            Jwts.parserBuilder()
                    .setSigningKey(JwtTokenUtils.JWT_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            System.out.println("토큰 검증 실패!! 토큰(" + authToken + ")");
            e.printStackTrace();
        }
        return false;
    }


}
