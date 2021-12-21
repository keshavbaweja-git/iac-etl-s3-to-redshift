package com.myorg;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.List;

import static software.amazon.awscdk.services.ec2.SubnetType.*;

public class EtlS3ToRedshiftVpcStack extends Stack {
    public EtlS3ToRedshiftVpcStack(@Nullable Construct scope, @Nullable String id, @Nullable StackProps props) {
        super(scope, id, props);

        final Vpc ab302Vpc = Vpc.Builder.create(this, "Ab302VPC")
                                        .cidr("10.11.0.0/16")
                                        .maxAzs(3)
                                        .subnetConfiguration(List.of(SubnetConfiguration.builder()
                                                                                        .subnetType(PUBLIC)
                                                                                        .name("Public")
                                                                                        .cidrMask(24)
                                                                                        .build(),
                                                                     SubnetConfiguration.builder()
                                                                                        .cidrMask(24)
                                                                                        .name("Private")
                                                                                        .subnetType(PRIVATE_WITH_NAT)
                                                                                        .build(),
                                                                     SubnetConfiguration.builder()
                                                                                        .cidrMask(28)
                                                                                        .name("DB")
                                                                                        .subnetType(PRIVATE_ISOLATED)
                                                                                        .reserved(true)
                                                                                        .build()))
                                        .natGateways(3)
                                        .build();

        CfnOutput.Builder
                .create(this, "vpcId")
                .exportName("Ab302VpcId")
                .value(ab302Vpc.getVpcId());

        final SecurityGroup bastionHostSecurityGroup = SecurityGroup.Builder
                .create(this, "bastionHostSecurityGroup")
                .securityGroupName("Ab302BastionHostSG")
                .description("Bastion host security group")
                .vpc(ab302Vpc)
                .allowAllOutbound(true)
                .build();

        bastionHostSecurityGroup.getConnections()
                                .allowFrom(Peer.ipv4("54.240.199.108/32"), Port.tcp(22),
                                           "Allow SSH from my ip");

    }


}
