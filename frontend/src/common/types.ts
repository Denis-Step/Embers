/* {"user":  "BillyBobThornton",
  "publicToken":  "public-development-32d715cf-252e-44cb-a230-95267d9e85fa",
  "institutionId":  "6969",
  "availableProducts" :  ["transactions"],
  "dateCreated":  "2021-08-21T22:05:05",
  "metaData":  {"foo":  "bar"}
} */

import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";

export interface PlaidItemCreationInfo {
    user: string;
    publicToken: string;
    institutionId: string; // {INST_NAME}-{INST_ID} need both for Dynamo sort key.
    availableProducts: string[];
    accounts: string[];
    dateCreated: Date | string;
    metaData: string; // Stringfied metadata.
}