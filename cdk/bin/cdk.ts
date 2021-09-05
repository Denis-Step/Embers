#!/usr/bin/env node
import * as cdk from '@aws-cdk/core';
import {JPPipelineStack} from "../lib/pipeline";


const app = new cdk.App();
new JPPipelineStack(app, "JPrepo");
