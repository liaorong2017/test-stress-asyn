package org.raje.test.tcp;

import java.util.List;

import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

@Component
public class FixedLenghtMessageAdapter implements NettyMessageAdapter {
	final static long max_package_length = 10 * 1024;

	@Override
	public ChannelOutboundHandlerAdapter encoder() {
		return new MessageToByteEncoder<String>() {

			@Override
			protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
				byte[] req = msg.getBytes();
				// final byte[] reqBytes = new byte[4 + req.length];
				// for (int i = 0; i < 4; i++) {
				// reqBytes[i] = (byte) (req.length >> 8 * i & 0xFF);
				// }
				// System.arraycopy(req, 0, reqBytes, 4, req.length);
				out.writeBytes(req);
			}

		};
	}

	@Override
	public ChannelInboundHandlerAdapter decoder() {
		return new ByteToMessageDecoder() {

			@Override
			protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
				if (in.readableBytes() < 236) {
					return;
				}
				byte[] res = new byte[236];
				in.readBytes(res);
				out.add(new String(res, "GBK"));
			}

		};
	}

}
