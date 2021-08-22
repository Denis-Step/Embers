/* {"user":  "BillyBobThornton",
  "publicToken":  "public-development-32d715cf-252e-44cb-a230-95267d9e85fa",
  "institutionId":  "6969",
  "availableProducts" :  ["transactions"],
  "dateCreated":  "2021-08-21T22:05:05",
  "metaData":  {"foo":  "bar"}
} */

import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";

export interface PlaidItemCreationRequest {
    user: string;
    publicToken: string;
    institutionId: string;
    availableProducts: string[];
    dateCreated: Date | string;
    metaData: PlaidLinkOnSuccessMetadata
}