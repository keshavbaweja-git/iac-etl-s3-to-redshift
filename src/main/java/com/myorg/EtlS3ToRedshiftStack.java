package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;

public class EtlS3ToRedshiftStack extends Stack {
    public EtlS3ToRedshiftStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EtlS3ToRedshiftStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        final Bucket rawBucket = Bucket.Builder
                .create(this, "rawBucket")
                .bucketName("keshavkb2-ab302-raw")
                .build();
        CfnOutput.Builder
                .create(this, "rawBucketName")
                .exportName("RawBucketName")
                .value(rawBucket.getBucketName())
                .build();

        final Bucket processedBucket = Bucket.Builder
                .create(this, "processedBucket")
                .bucketName("keshavkb2-ab302-processed")
                .build();
        CfnOutput.Builder
                .create(this, "processedBucketName")
                .exportName("ProcessedBucketName")
                .value(processedBucket.getBucketName())
                .build();


    }
}
