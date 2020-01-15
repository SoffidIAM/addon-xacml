package com.soffid.iam.addons.xacml.sync.web;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import es.caib.seycon.ng.exception.InternalErrorException;


public class TokenVerifier {
	public Map<String, Object> verify (String signedToken) throws Exception
	{
		DecodedJWT d = JWT.decode(signedToken);
		
		
		String issuer = d.getIssuer();
		if (issuer == null)
			throw new InternalErrorException("Missing issuer in token attributes");
		
		boolean verified = false;
		UrlJwkProvider jwkProvider = new UrlJwkProvider(issuer);
		if (d.getKeyId() != null)
		{
			Jwk key = jwkProvider.get( d.getKeyId() );
			JWT.require(createAlgorithm (key))
				.acceptLeeway(300) // 5 minutes
				.build()
				.verify(d);
			verified = true;
		}
		else
		{
			for ( Jwk key: jwkProvider.getAll())
			{
				if (key.getUsage() == null || key.getUsage().equals("sig"))
				{
					
					JWT.require(createAlgorithm (key))
					.acceptLeeway(300) // 5 minutes
					.build()
					.verify(d);
					verified = true;
				}
					
			}
		}
		
		if ( !verified )
			throw new InternalErrorException("Missing key");
			
		System.out.println(d.getIssuer());
		

		HashMap<String, Object> o = new HashMap<String,Object>();
		o.put("iss", d.getIssuer());
		o.put("sub", d.getSubject());
		o.put("aud", d.getAudience());
		o.put("exp", d.getExpiresAt());
		o.put("typ", d.getType());
		for (Entry<String, Claim> claim: d.getClaims().entrySet())
		{
			o.put(claim.getKey(), claim.getValue().asString());
		}
		return o;
	}

	private Algorithm createAlgorithm(Jwk key) throws NoSuchAlgorithmException, InvalidKeySpecException, InternalErrorException, InvalidParameterSpecException {
		if (key.getType().equals("RSA"))
		{
			KeyFactory kf = KeyFactory.getInstance("RSA");
			BigInteger modulus = new BigInteger(1, java.util.Base64.getUrlDecoder().decode((String) key.getAdditionalAttributes().get("n")));
			BigInteger exponent = new BigInteger(1, java.util.Base64.getUrlDecoder().decode((String) key.getAdditionalAttributes().get("e")));
			PublicKey pk = kf.generatePublic(new RSAPublicKeySpec(modulus, exponent));
			if (key.getAlgorithm().equals("RS256"))
				return Algorithm.RSA256((RSAPublicKey) pk, null);
			else if (key.getAlgorithm().equals("RS384"))
				return Algorithm.RSA384((RSAPublicKey) pk, null);
			else if (key.getAlgorithm().equals("RS512"))
				return Algorithm.RSA512((RSAPublicKey) pk, null);
			else
				throw new InternalErrorException ("Unsupported algorithm "+key.getAlgorithm()+" for key type "+key.getType());
		}
		else if (key.getType().equals("EC"))
		{
			KeyFactory kf = KeyFactory.getInstance("EC");
			String crv =  (String) key.getAdditionalAttributes().get("crv");
			BigInteger x = new BigInteger(1, java.util.Base64.getUrlDecoder().decode((String) key.getAdditionalAttributes().get("x")));
			BigInteger y = new BigInteger(1, java.util.Base64.getUrlDecoder().decode((String) key.getAdditionalAttributes().get("y")));
			ECPoint p = new ECPoint(x, y);
			AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
			if (key.getAlgorithm().equals("ES256"))
			{
				parameters.init(new ECGenParameterSpec("secp256r1"));
				ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
				PublicKey pk = kf.generatePublic(new ECPublicKeySpec( p, ecParameterSpec ));
				return Algorithm.ECDSA256((ECPublicKey) pk, null);
			}
			else if (key.getAlgorithm().equals("ES384"))
			{
				parameters.init(new ECGenParameterSpec("secp384r1"));
				ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
				PublicKey pk = kf.generatePublic(new ECPublicKeySpec( p, ecParameterSpec ));
				return Algorithm.ECDSA384((ECPublicKey) pk, null);
			}
			else if (key.getAlgorithm().equals("ES521"))
			{
				parameters.init(new ECGenParameterSpec("secp521r1"));
				ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
				PublicKey pk = kf.generatePublic(new ECPublicKeySpec( p, ecParameterSpec ));
				return Algorithm.ECDSA512((ECPublicKey) pk, null);
			}
			else
				throw new InternalErrorException ("Unsupported algorithm "+key.getAlgorithm()+" for key type "+key.getType());
		}
		else
			throw new InternalErrorException ("Unsupported algorithm "+key.getAlgorithm()+" for key type "+key.getType());
	}
}