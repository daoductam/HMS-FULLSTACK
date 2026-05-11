package com.hms.media;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class MediaApplication {
    public static void main(String ... args) {
        Quarkus.run(args);
    }
}
