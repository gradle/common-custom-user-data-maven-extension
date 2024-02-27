package com.gradle.ccud.adapters;

import java.util.List;

public interface BuildResultAdapter {

    List<Throwable> getFailures();

}
