#!/usr/bin/env node
import * as cdk from '@aws-cdk/core';
import { LambdaStack } from '../lib/cdk-stack';



const app = new cdk.App();
new LambdaStack(app, 'JavaPlaidStack');
