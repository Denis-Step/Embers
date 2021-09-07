#!/usr/bin/env node
import * as cdk from '@aws-cdk/core';
import {JPPipelineStack} from "../lib/pipeline";
import {LambdaStack} from "../lib/cdk-stack";


const app = new cdk.App();
new LambdaStack(app, "JPrepo");
