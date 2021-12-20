package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyDocument;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;

import static java.util.Arrays.asList;
import static software.amazon.awscdk.services.iam.Effect.ALLOW;

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

        final PolicyDocument s3ManagedPolicyDocumentAb302 = PolicyDocument.Builder
                .create()
                .statements(asList(PolicyStatement
                                .Builder
                                .create()
                                .effect(ALLOW)
                                .actions(asList("s3:GetObject",
                                        "s3:PutObject"))
                                .resources(asList("arn:aws:s3:::" + rawBucket.getBucketName() + "*",
                                        "arn:aws:s3:::" + processedBucket.getBucketName() + "*"))
                                .build()))
                .build();

        final ManagedPolicy s3ManagedPolicyAb302 = ManagedPolicy.Builder
                .create(this, "s3ManagedPolicyAb302")
                .document(s3ManagedPolicyDocumentAb302)
                .managedPolicyName("S3ManagedPolicyAb302")
                .build();

        CfnOutput.Builder
                .create(this, "s3ManagedPolicyAb302Name")
                .exportName("S3ManagedPolicyAb302Name")
                .value(s3ManagedPolicyAb302.getManagedPolicyName())
                .build();
    }
}
