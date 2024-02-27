package com.gradle.ccud.adapters;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BuildScanDataObfuscationAdapter {

    void username(Function<? super String, ? extends String> obfuscator);

    void hostname(Function<? super String, ? extends String> obfuscator);

    void ipAddresses(Function<? super List<InetAddress>, ? extends List<String>> obfuscator);

    @FunctionalInterface
    interface ObfuscatorConsumer<I, O> extends Consumer<Function<? super I, ? extends O>> {
    }

}
