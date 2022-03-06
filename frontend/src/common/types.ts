export interface PlaidItemCreationInfo {
    publicToken: string;
    institutionId: string; // {INST_NAME}-{INST_ID} need both for Dynamo sort key.
    availableProducts: string[];
    accounts: string[];
    dateCreated: Date | string;
    webhookEnabled: boolean;
    metaData: string; // Stringfied metadata.
}

export interface CognitoJwt {
    access_token: string;
    id_token: string;
    token_type: string;
    expires_at: Date;
}

export interface Transaction {
    user: string;
    institutionName: string;
    accountId: string;
    amount: number;
    description: string;
    originalDescription?: string;
    merchantName: string;
    date: string;
    transactionId: string
}