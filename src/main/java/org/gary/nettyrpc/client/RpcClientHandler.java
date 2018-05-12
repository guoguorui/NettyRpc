package org.gary.nettyrpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.common.SerializeUtils;

//处理完从服务器收到的字节被译码通道转换成了RpcResponse,在channelRead0的参数中
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

	private ByteBuf clientMessage;

	public RpcClientHandler(RpcRequest rpcRequest) {
		byte[] req = SerializeUtils.serialize(rpcRequest,RpcRequest.class);
		clientMessage = Unpooled.buffer(req.length);
		clientMessage.writeBytes(req);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(clientMessage);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		Object message = SerializeUtils.deserialize(req, RpcResponse.class).getResult();
		// String message = new String(req,"UTF-8");
		System.out.println(message);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
