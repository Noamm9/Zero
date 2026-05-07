package com.ricedotwho.zero.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ConnectionInfo {
    private final UUID uuid;
    @Setter
    private String host;
    @Setter
    private int port;
}