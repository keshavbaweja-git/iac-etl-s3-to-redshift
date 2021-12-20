package com.myorg;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.services.iam.*;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static software.amazon.awscdk.services.iam.Effect.ALLOW;
import static software.amazon.awscdk.services.iam.ManagedPolicy.*;

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
                .path("/service-role/")
                .document(s3ManagedPolicyDocumentAb302)
                .managedPolicyName("S3ManagedPolicyAb302")
                .build();

        CfnOutput.Builder
                .create(this, "s3ManagedPolicyAb302Name")
                .exportName("S3ManagedPolicyAb302Name")
                .value(s3ManagedPolicyAb302.getManagedPolicyArn())
                .build();

        final String LAKE_FORMATION_WORKFLOW_ROLE_NAME = "LakeFormationWorkflowRoleAb302";
        final PolicyDocument lakeFormationWorkflowPolicyDocument = PolicyDocument.Builder
                .create()
                .statements(asList(PolicyStatement
                                           .Builder
                                           .create()
                                           .effect(ALLOW)
                                           .actions(asList("lakeformation:GetDataAccess",
                                                           "lakeformation:GrantPermissions"))
                                           .resources(asList("*"))
                                           .build(),
                                   PolicyStatement
                                           .Builder
                                           .create()
                                           .effect(ALLOW)
                                           .actions(asList("iam:PassRole"))
                                           .resources(asList("arn:aws:iam::"
                                                                     + this.getAccount()
                                                                     + ":role/" + LAKE_FORMATION_WORKFLOW_ROLE_NAME))
                                           .build()))
                .build();

        final Map<String, PolicyDocument> lakeFormationWorkflowPolicyDocuments = new HashMap<>();
        lakeFormationWorkflowPolicyDocuments.put("LakeFormationWorkflow",
                                                 lakeFormationWorkflowPolicyDocument);
        final Role lakeFormationWorkflowRole = Role.Builder
                .create(this, "lakeFormationWorkflowRole")
                .roleName(LAKE_FORMATION_WORKFLOW_ROLE_NAME)
                .assumedBy(new ServicePrincipal("glue.amazonaws.com"))
                .managedPolicies(asList(fromAwsManagedPolicyName("service-role/AWSGlueServiceRole")))
                .inlinePolicies(lakeFormationWorkflowPolicyDocuments)
                .build();

        s3ManagedPolicyAb302.attachToRole(lakeFormationWorkflowRole);

        CfnOutput.Builder
                .create(this, "lakeFormationWorkflowRoleArn")
                .exportName(LAKE_FORMATION_WORKFLOW_ROLE_NAME + "Arn")
                .value(lakeFormationWorkflowRole.getRoleArn())
                .build();

    }
}
