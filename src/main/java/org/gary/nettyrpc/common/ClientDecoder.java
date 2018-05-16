package org.gary.nettyrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.gary.nettyrpc.carrier.RpcResponse;

public class ClientDecoder extends LengthFieldBasedFrameDecoder {

    public ClientDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    private int length;

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in == null) {
            return null;
        }
        if (in.readableBytes() < 4) {
            //throw new Exception("可读信息段比头部信息都小，你在逗我？");
            return null;
        }
        //注意在读的过程中，readIndex的指针也在移动
        if(length==0)
            length=in.readInt();
        if (in.readableBytes() < length) {
            //throw new Exception("body字段你告诉我长度是"+length+",但是真实情况是没有这么多，你又逗我？");
            return null;
        }
        ByteBuf buf = in.readBytes(length);
        byte[] request = new byte[buf.readableBytes()];
        buf.readBytes(request);
        length=0;
        return SerializeUtils.deserialize(request, RpcResponse.class);
    }
}
