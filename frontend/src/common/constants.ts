export const BETA_ENDPOINT = "https://4oxqcp9xmi.execute-api.us-east-2.amazonaws.com/prod/"
export const LINK_API_RESOURCE = "linktoken";
export const ITEM_API_RESOURCE = "items";
export const TRANSACTIONS_API_RESOURCE = "transactions";

export const LINK_DEFAULT_PRODUCTS = ["transactions"]

export const COGNITO_UI_URI = `https://txnotificationservice.auth.us-east-2.amazoncognito.com/login?
client_id=6jq6qnv23ph0jp6q48coikr5vt&response_type=token&scope=email+openid+phone+profile&
redirect_uri=http://localhost:3000/googlelogin`
export const IDENTITY_POOL_ID = "arn:aws:cognito-idp:us-east-2:397250182609:userpool/us-east-2_AkKHjmqmM";
export const GOOGLE_AUTH_CLIENT_ID = "429859947215-buj14vnqmaclu9lehui11t125gc1elj4.apps.googleusercontent.com";