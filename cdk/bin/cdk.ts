#!/usr/bin/env node
import * as cdk from '@aws-cdk/core';
import {JPPipelineStack} from "../lib/PipelineStack";

/* ENTRY POINT */

const app = new cdk.App();
//new MainStack(app, 'test')
new JPPipelineStack(app, 'JPPipeline');
