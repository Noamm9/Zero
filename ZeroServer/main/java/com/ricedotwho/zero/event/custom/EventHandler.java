package com.ricedotwho.zero.event.custom;

import com.ricedotwho.mcprotocol.protocol.packet.Packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {
    boolean async() default false;
    Class<? extends Packet> value() default Packet.class;
}