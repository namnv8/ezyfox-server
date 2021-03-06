package com.tvd12.ezyfoxserver.nio.testing.wrapper;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutorService;

import org.eclipse.jetty.websocket.api.Session;
import org.testng.annotations.Test;

import com.tvd12.ezyfox.codec.EzyStringToObjectDecoder;
import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfoxserver.EzySimpleServer;
import com.tvd12.ezyfoxserver.codec.EzyCodecFactory;
import com.tvd12.ezyfoxserver.constant.EzyCommand;
import com.tvd12.ezyfoxserver.constant.EzyConnectionType;
import com.tvd12.ezyfoxserver.context.EzySimpleServerContext;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.nio.builder.impl.EzyHandlerGroupBuilderFactoryImpl;
import com.tvd12.ezyfoxserver.nio.factory.EzyHandlerGroupBuilderFactory;
import com.tvd12.ezyfoxserver.nio.handler.EzyHandlerGroup;
import com.tvd12.ezyfoxserver.nio.wrapper.EzyHandlerGroupManager;
import com.tvd12.ezyfoxserver.nio.wrapper.EzyNioSessionManager;
import com.tvd12.ezyfoxserver.nio.wrapper.impl.EzyHandlerGroupManagerImpl;
import com.tvd12.ezyfoxserver.nio.wrapper.impl.EzyNioSessionManagerImpl;
import com.tvd12.ezyfoxserver.service.impl.EzySimpleSessionTokenGenerator;
import com.tvd12.ezyfoxserver.setting.EzySimpleSettings;
import com.tvd12.ezyfoxserver.setting.EzySimpleStreamingSetting;
import com.tvd12.ezyfoxserver.socket.EzyBlockingSessionTicketsQueue;
import com.tvd12.ezyfoxserver.socket.EzyBlockingSocketDisconnectionQueue;
import com.tvd12.ezyfoxserver.socket.EzyBlockingSocketStreamQueue;
import com.tvd12.ezyfoxserver.socket.EzyChannel;
import com.tvd12.ezyfoxserver.socket.EzySessionTicketsQueue;
import com.tvd12.ezyfoxserver.socket.EzySimpleSocketRequestQueues;
import com.tvd12.ezyfoxserver.socket.EzySocketDisconnectionQueue;
import com.tvd12.ezyfoxserver.socket.EzySocketRequestQueues;
import com.tvd12.ezyfoxserver.socket.EzySocketStreamQueue;
import com.tvd12.ezyfoxserver.statistics.EzySimpleStatistics;
import com.tvd12.ezyfoxserver.statistics.EzyStatistics;
import com.tvd12.test.base.BaseTest;

public class EzyHandlerGroupManagerImplTest extends BaseTest {

	@Test
	public void test() {
		EzyNioSessionManager sessionManager = (EzyNioSessionManager)EzyNioSessionManagerImpl.builder()
				.tokenGenerator(new EzySimpleSessionTokenGenerator())
				.build();
		ExEzyByteToObjectDecoder decoder = new ExEzyByteToObjectDecoder();
		EzyCodecFactory codecFactory = mock(EzyCodecFactory.class);
		when(codecFactory.newDecoder(any())).thenReturn(decoder);
		ExecutorService statsThreadPool = EzyExecutors.newSingleThreadExecutor("stats");
		ExecutorService codecThreadPool = EzyExecutors.newSingleThreadExecutor("codec");
		EzySocketRequestQueues requestQueues = new EzySimpleSocketRequestQueues();
		EzySocketStreamQueue streamQueue = new EzyBlockingSocketStreamQueue();
		EzySocketDisconnectionQueue disconnectionQueue = EzyBlockingSocketDisconnectionQueue.getInstance();
		
		EzySimpleSettings settings = new EzySimpleSettings();
		EzySimpleStreamingSetting streaming = settings.getStreaming();
		streaming.setEnable(true);
		EzySimpleServer server = new EzySimpleServer();
		server.setSettings(settings);
		server.setSessionManager(sessionManager);
		EzySimpleServerContext serverContext = new EzySimpleServerContext();
		serverContext.setServer(server);
		serverContext.init();
		
		EzySessionTicketsQueue socketSessionTicketsQueue = new EzyBlockingSessionTicketsQueue();
		EzySessionTicketsQueue webSocketSessionTicketsQueue = new EzyBlockingSessionTicketsQueue();
		EzyStatistics statistics = new EzySimpleStatistics();
		EzyHandlerGroupBuilderFactory handlerGroupBuilderFactory = EzyHandlerGroupBuilderFactoryImpl.builder()
				.statistics(statistics)
				.socketSessionTicketsQueue(socketSessionTicketsQueue)
				.webSocketSessionTicketsQueue(webSocketSessionTicketsQueue)
				.build();
		
		EzyHandlerGroupManager handlerGroupManager = EzyHandlerGroupManagerImpl.builder()
				.statsThreadPool(statsThreadPool)
				.codecThreadPool(codecThreadPool)
				.streamQueue(streamQueue)
				.requestQueues(requestQueues)
				.disconnectionQueue(disconnectionQueue)
				.codecFactory(codecFactory)
				.serverContext(serverContext)
				.handlerGroupBuilderFactory(handlerGroupBuilderFactory)
				.build();
		handlerGroupManager.removeHandlerGroup(null);
		EzySession session1 = mock(EzySession.class);
		handlerGroupManager.removeHandlerGroup(session1);
		EzySession session2 = mock(EzySession.class);
		EzyChannel channel2 = mock(EzyChannel.class);
		when(session2.getChannel()).thenReturn(channel2);
		handlerGroupManager.removeHandlerGroup(session2);
		EzyChannel channel3 = mock(EzyChannel.class);
		Session connection3 = mock(Session.class);
		when(channel3.getConnection()).thenReturn(connection3);
		EzyHandlerGroup handlerGroup3 = handlerGroupManager.newHandlerGroup(channel3, EzyConnectionType.WEBSOCKET);
		EzySession session3 = mock(EzySession.class);
		when(session3.getChannel()).thenReturn(channel3);
		assert handlerGroupManager.getDataHandlerGroup(null) == null;
		assert handlerGroupManager.getDataHandlerGroup(session1) == null;
		assert handlerGroupManager.getDataHandlerGroup(session2) == null;
		assert handlerGroupManager.getWriterGroup(session3) == handlerGroup3;
		handlerGroupManager.removeHandlerGroup(session3);
		handlerGroupManager.destroy();
	}
	
	public static class ExEzyByteToObjectDecoder implements EzyStringToObjectDecoder {

		@Override
		public Object decode(String bytes) throws Exception {
			return EzyEntityFactory.newArrayBuilder()
					.append(EzyCommand.PING.getId())
					.build();
		}

		@Override
		public Object decode(byte[] bytes) throws Exception {
			return EzyEntityFactory.newArrayBuilder()
					.append(EzyCommand.PING.getId())
					.build();
		}
		
	}
	
}
