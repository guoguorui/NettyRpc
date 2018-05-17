package org.gary.nettyrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.gary.nettyrpc.carrier.RpcRequest;

public class ServerDecoder extends LengthFieldBasedFrameDecoder {

    public ServerDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(1024 * 1024, 0, 4);
    }

    private int length;

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null)
            return null;
        if (in.readableBytes() < 4)
            return null;
        //注意在读的过程中，readIndex的指针也在移动
        if (length == 0)
            length = in.readInt();
        if (in.readableBytes() < length)
            return null;
        ByteBuf buf = in.readBytes(length);
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        length = 0;
        return SerializeUtils.deserialize(request, RpcRequest.class);
    }
}
