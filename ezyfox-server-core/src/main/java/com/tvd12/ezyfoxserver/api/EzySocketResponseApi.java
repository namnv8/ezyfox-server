package com.tvd12.ezyfoxserver.api;

import com.tvd12.ezyfox.codec.EzyMessageDataEncoder;
import com.tvd12.ezyfox.codec.EzyObjectToByteEncoder;
import com.tvd12.ezyfox.codec.EzySimpleMessageDataEncoder;
import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.constant.EzyConnectionType;

public class EzySocketResponseApi extends EzyAbstractResponseApi {

	protected final EzyMessageDataEncoder encoder;
	
	public EzySocketResponseApi(Object encoder) {
		this.encoder = new EzySimpleMessageDataEncoder((EzyObjectToByteEncoder)encoder);
	}
	
	@Override
	protected Object encodeData(EzyArray data) throws Exception {
		Object answer = encoder.encode(data);
		return answer;
	}
	
	@Override
	protected EzyConstant getConnectionType() {
		return EzyConnectionType.SOCKET;
	}
	
}
