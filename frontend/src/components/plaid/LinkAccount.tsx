import React, {useCallback, useMemo, useReducer, useEffect, useContext} from 'react';
import {Button, Checkbox, FormControl, FormLabel, Input, FormHelperText, VStack} from "@chakra-ui/react"
import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";
import {getLinkToken, requestItemCreation} from "../../common/apicalls";
import LinkFlow from "./LinkFlow";
import {PlaidItemCreationInfo} from "../../common/types";
import {AuthContext, useAuth} from "../../contexts/cognitoAuthContext";


// @TODO: Use discriminated unions to type reducer actions.
enum ActionKind {
    UpdateUser = "UPDATEUSER",
    UpdateLinkToken = "UPDATELINKTOKEN",
    UpdatePublicToken = "UPDATEPUBLICTOKEN",
    UpdateWebhook = "UPDATEWEBHOOK"
}

type Action = {
    type: ActionKind,
    payload: any
}

type State = {
    products: string[];
    linkToken?: string;
    publicToken?: string;
    metaData?: PlaidLinkOnSuccessMetadata;
    webhookEnabled: boolean
}

const initialState: State = {
    products: ["transactions"], // Default products. Not changed for now.
    webhookEnabled: false
};

function linkReducer(state: State, action: Action): State {
    switch (action.type) {
        case ActionKind.UpdateLinkToken:
            return {
                ...state,
                linkToken: action.payload.linkToken
            }
        case ActionKind.UpdatePublicToken:
            return {
                ...state,
                publicToken: action.payload.publicToken,
                metaData: action.payload.metaData
            }
        case ActionKind.UpdateWebhook:
            return {
                ...state,
                webhookEnabled: action.payload.webhook
            }
        default:
            return {...state}
    }
}

// Helper function to build object to send request for a new item.
const buildItemInfo = (metadata: Partial<PlaidLinkOnSuccessMetadata>,
                       publicToken: string,
                       webhookEnabled: boolean,
                       products: string[]): PlaidItemCreationInfo => {

    // No institutionId means Dummy string.
    const institutionId = metadata.institution?.institution_id || "0000";
    const institutionName = metadata.institution?.name || "UNNAMED";

    // {ACCNAME}-{ACC_ID}
    const accounts = metadata.accounts?.map((account) => account.name + "-" + account.id) || [];

    return {
        publicToken: publicToken,
        institutionId: institutionName + "-" + institutionId,
        availableProducts: products,
        accounts: accounts,
        dateCreated: new Date().toISOString(),
        webhookEnabled: webhookEnabled,
        metaData: JSON.stringify(metadata)
    };
}


const LinkAccount = () => {
    // State for getLinkToken params.
    const [state, dispatch] = useReducer(linkReducer, initialState);
    const auth = useAuth();

    useEffect( () => {

        const sendInfoBack = async (infoToSend: PlaidItemCreationInfo) => {
            console.log('infoToSend', infoToSend);
            const response = await requestItemCreation(infoToSend, auth.token.id_token);
            console.log(response)
        }

        // Send info back when publicToken is generated.
        if (state.publicToken && state.metaData) {
            const {metaData, publicToken, webhookEnabled, products} = state;
            const infoToSend = buildItemInfo(metaData, publicToken, webhookEnabled, products)
            sendInfoBack(infoToSend);
        }

    }, [state])

    const updateUser = (event: React.FormEvent<HTMLInputElement>): void => {
        const input = event.currentTarget.value;
        dispatch({type: ActionKind.UpdateUser, payload: input});
    };

    const updateWebhook = useCallback( (): void => {
        dispatch({type: ActionKind.UpdateWebhook, payload: {webhookEnabled: !state.webhookEnabled}});
    }, [state.webhookEnabled])

    // Handler for link token button.
    const updateLinkToken = useCallback(async () => {
        const link = await getLinkToken(auth.token.id_token, state.webhookEnabled,  state.products);
        dispatch({type: ActionKind.UpdateLinkToken, payload: link});
    }, [state.products, state.webhookEnabled, auth]);

    // onSuccess callback for LinkFlow to initiate item creation server-side.
    const onLinkSuccess = useCallback(async (public_token: string,
                                             metadata: PlaidLinkOnSuccessMetadata) => {
        console.log('on link success called', public_token, metadata);
        dispatch({
            type: ActionKind.UpdatePublicToken,
            payload: {publicToken: public_token, metaData: metadata}
        });
    }, [])

    // Fire off link flow iff there is a linkToken generated.
    const linkFlow = useMemo(() => {
            if (state.linkToken) {
                return (<LinkFlow link_token={state.linkToken}
                                  onSuccess={onLinkSuccess}/>)
            } else {
                return null;
            }
        },
        [state.linkToken, onLinkSuccess])

    return (
        <div id="link-token-creation">
            <VStack>
            <Checkbox size="sm" colorScheme="red" defaultIsChecked={false} onChange = {
                (e) => { updateWebhook() }}/>
            <Button colorScheme="teal"
                    size="md"
                    onClick={updateLinkToken}>
                Get Link Token
            </Button>
            </VStack>
            {linkFlow}
        </div>
    )

}

export default LinkAccount;
