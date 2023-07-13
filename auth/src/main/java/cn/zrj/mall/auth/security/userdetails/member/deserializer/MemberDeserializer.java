package cn.zrj.mall.auth.security.userdetails.member.deserializer;


import cn.zrj.mall.auth.security.userdetails.member.MemberUserDetails;
import cn.zrj.mall.auth.security.userdetails.user.SysUserDetails;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Set;


/**
 * @author zhaorujie
 */
public class MemberDeserializer extends JsonDeserializer<MemberUserDetails> {

	@Override
	public MemberUserDetails deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		JsonNode jsonNode = mapper.readTree(jsonParser);
		JsonNode passwordNode = readJsonNode(jsonNode, "password");
		Long memberId = readJsonNode(jsonNode, "memberId").asLong();
		String username = readJsonNode(jsonNode, "username").asText();
		String mobile = readJsonNode(jsonNode, "mobile").asText();
		boolean enabled = readJsonNode(jsonNode, "enabled").asBoolean();
		return new MemberUserDetails(memberId, username, enabled, mobile);
	}

	private JsonNode readJsonNode(JsonNode jsonNode, String field) {
		return jsonNode.has(field) ? jsonNode.get(field) : MissingNode.getInstance();
	}
}