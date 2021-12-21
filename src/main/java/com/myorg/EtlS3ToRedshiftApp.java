package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class EtlS3ToRedshiftApp {
    public static void main(final String[] args) {
        App app = new App();

        new EtlS3ToRedshiftStack(app, "EtlS3ToRedshiftStack", StackProps.builder()
                                                                        .build());

        new EtlS3ToRedshiftVpcStack(app, "EtlS3ToRedshiftVpcStack", StackProps.builder()
                                                                              .build());

        app.synth();
    }
}

