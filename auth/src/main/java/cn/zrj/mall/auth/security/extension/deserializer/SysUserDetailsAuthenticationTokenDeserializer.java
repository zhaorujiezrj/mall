package cn.zrj.mall.auth.security.extension.deserializer;


import cn.zrj.mall.auth.security.userdetails.user.SysUserDetails;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

/**
 * @author zhaorujie
 */
public class SysUserDetailsAuthenticationTokenDeserializer extends JsonDeserializer<UsernamePasswordAuthenticationToken> {

	private static final TypeReference<List<GrantedAuthority>> GRANTED_AUTHORITY_LIST = new TypeReference<List<GrantedAuthority>>() {
	};

	private static final TypeReference<Object> OBJECT = new TypeReference<Object>() {
	};

	@Override
	public UsernamePasswordAuthenticationToken deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		JsonNode jsonNode = mapper.readTree(jp);
		boolean authenticated = readJsonNode(jsonNode, "authenticated").asBoolean();
		JsonNode principalNode = readJsonNode(jsonNode, "principal");
		//Object principal = getPrincipal(mapper, principalNode);
		JsonNode credentialsNode = readJsonNode(jsonNode, "credentials");
		Object credentials = getCredentials(credentialsNode);
		List<GrantedAuthority> authorities = mapper.readValue(readJsonNode(jsonNode, "authorities").traverse(mapper),
				GRANTED_AUTHORITY_LIST);
		SysUserDetails principal = new SysUserDetails(
				readJsonNode(principalNode, "userId").asLong(),
				readJsonNode(principalNode, "username").asText(),
				readJsonNode(principalNode, "password").asText(),
				readJsonNode(principalNode, "enabled").asBoolean());
		UsernamePasswordAuthenticationToken token = (!authenticated)
				? new UsernamePasswordAuthenticationToken(principal, credentials)
				: new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
		JsonNode detailsNode = readJsonNode(jsonNode, "details");
		if (detailsNode.isNull() || detailsNode.isMissingNode()) {
			token.setDetails(null);
		}
		else {
			Object details = mapper.readValue(detailsNode.toString(), OBJECT);
			token.setDetails(details);
		}
		return token;
	}

	private Object getCredentials(JsonNode credentialsNode) {
		if (credentialsNode.isNull() || credentialsNode.isMissingNode()) {
			return null;
		}
		return credentialsNode.asText();
	}


	private Object getPrincipal(ObjectMapper mapper, JsonNode principalNode)
			throws IOException, JsonParseException, JsonMappingException {
		if (principalNode.isObject()) {
			return mapper.readValue(principalNode.traverse(mapper), SysUserDetails.class);
		}
		return principalNode.asText();
	}

	private JsonNode readJsonNode(JsonNode jsonNode, String field) {
		return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
	}
}