package org.gary.nettyrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.gary.nettyrpc.carrier.RpcRequest;

public class ClientEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        if (msg == null)
            throw new Exception("msg is null");
        byte[] request = SerializeUtil.serialize(msg, RpcRequest.class);
        out.writeInt(request.length);
        out.writeBytes(request);
    }
}
