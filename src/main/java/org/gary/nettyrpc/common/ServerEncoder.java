package org.gary.nettyrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.gary.nettyrpc.carrier.RpcResponse;

public class ServerEncoder extends MessageToByteEncoder<RpcResponse>{

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {
        if(msg==null)
            throw new Exception("msg is null");
        byte[] request=SerializeUtils.serialize(msg,RpcResponse.class);
        out.writeInt(request.length);
        out.writeBytes(request);
    }
}
