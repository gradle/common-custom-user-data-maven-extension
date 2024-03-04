package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.BuildScanDataObfuscationAdapter;

import java.net.InetAddress;
import java.util.List;
import java.util.function.Function;

public class DefaultBuildScanDataObfuscationAdapter implements BuildScanDataObfuscationAdapter {

    private final ObfuscatorConsumer<String, String> usernameObfuscator;
    private final ObfuscatorConsumer<String, String> hostnameObfuscator;
    private final ObfuscatorConsumer<List<InetAddress>, List<String>> ipAddressesObfuscator;

    public DefaultBuildScanDataObfuscationAdapter(
        ObfuscatorConsumer<String, String> usernameObfuscator,
        ObfuscatorConsumer<String, String> hostnameObfuscator,
        ObfuscatorConsumer<List<InetAddress>, List<String>> ipAddressesObfuscator
    ) {
        this.usernameObfuscator = usernameObfuscator;
        this.hostnameObfuscator = hostnameObfuscator;
        this.ipAddressesObfuscator = ipAddressesObfuscator;
    }

    @Override
    public void username(Function<? super String, ? extends String> obfuscator) {
        usernameObfuscator.accept(obfuscator);
    }

    @Override
    public void hostname(Function<? super String, ? extends String> obfuscator) {
        hostnameObfuscator.accept(obfuscator);
    }

    @Override
    public void ipAddresses(Function<? super List<InetAddress>, ? extends List<String>> obfuscator) {
        ipAddressesObfuscator.accept(obfuscator);
    }
}
