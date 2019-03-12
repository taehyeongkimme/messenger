package com.kh.messenger.client;

import java.util.HashMap;
import java.util.Map;

import com.kh.messenger.common.MemberDTO;

public class Member {
	
	private static Map<String, MemberDTO> mem;
	
	static {
		mem = new HashMap<>();
		MemberDTO memberDTO = new MemberDTO();
		
		memberDTO.setId("admin");
		memberDTO.setPw("admin");
		memberDTO.setNickName("관리자");
		memberDTO.setTel("010-1111-1111");
		memberDTO.setBirth("2018-11-01");
		
		mem.put("admin", memberDTO);
	}
	
	private Member() { }
	
	public static Map<String,MemberDTO> getInstance() {
		return  mem;
	}
	
	
}
