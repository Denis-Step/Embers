import React, {useCallback, useMemo, useReducer, useEffect, useContext} from 'react';
import {Button, FormControl, FormLabel, Input, FormHelperText} from "@chakra-ui/react"
import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";
import {getLinkToken, requestItemCreation} from "../common/apicalls";
import LinkFlow from "./LinkFlow";
import {PlaidItemCreationInfo} from "../common/types";
import {AuthContext} from "../contexts/cognitoAuthContext";


// @TODO: Use discriminated unions to type reducer actions.
enum ActionKind {
    UpdateUser = "UPDATEUSER",
    UpdateLinkToken = "UPDATELINKTOKEN",
    UpdatePublicToken = "UPDATEPUBLICTOKEN"
}

type Action = {
    type: ActionKind,
    payload: any
}

type State = {
    user: string;
    products: string[];
    linkToken?: string;
    publicToken?: string;
    metaData?: PlaidLinkOnSuccessMetadata;
}

const initialState: State = {
    user: "",
    products: ["transactions"] // Default products. Not changed for now.
};

function linkReducer(state: State, action: Action): State {
    switch (action.type) {
        case ActionKind.UpdateUser:
            return {...state, user: action.payload};
        case ActionKind.UpdateLinkToken:
            return {...state, linkToken: action.payload};
        case ActionKind.UpdatePublicToken:
            return {
                ...state,
                publicToken: action.payload.publicToken,
                metaData: action.payload.metaData
            }
    }
}

// Helper function to build object to send request for a new item.
const buildItemInfo = (metadata: Partial<PlaidLinkOnSuccessMetadata>,
                           user: string,
                           publicToken: string,
                           products: string[]): PlaidItemCreationInfo => {

    // No institutionId means Dummy string.
    const institutionId = metadata.institution?.institution_id || "0000";
    const institutionName = metadata.institution?.name || "UNNAMED";

    // {ACCNAME}-{ACC_ID}
    const accounts = metadata.accounts?.map((account) => account.name + "-" + account.id) || [];

    return {
        user: user,
        publicToken: publicToken,
        institutionId: institutionName + "-" + institutionId,
        availableProducts: products,
        accounts: accounts,
        dateCreated: new Date().toISOString(),
        metaData: JSON.stringify(metadata)
    };
}


const LinkAccount = () => {
    // State for getLinkToken params.
    const [state, dispatch] = useReducer(linkReducer, initialState);
    const authContext = useContext(AuthContext);

    useEffect( () => {

        const sendInfoBack = async (infoToSend: PlaidItemCreationInfo) => {
            console.log('infoToSend', infoToSend);
            const response = await requestItemCreation(infoToSend);
            console.log(response)
        }

        // Send info back when publicToken is generated.
        if (state.publicToken && state.metaData) {
            const {metaData, user, publicToken, products} = state;
            const infoToSend = buildItemInfo(metaData, user, publicToken, products)
            sendInfoBack(infoToSend);
        }

    }, [state])

    const updateUser = (event: React.FormEvent<HTMLInputElement>): void => {
        const input = event.currentTarget.value;
        dispatch({type: ActionKind.UpdateUser, payload: input});
    };

    // Handler for link token button.
    const updateLinkToken = useCallback(async () => {
        if (state.user) {
            const link = await getLinkToken(state.user, authContext?.token.id_token!, state.products);
            dispatch({type: ActionKind.UpdateLinkToken, payload: link});
        }
    }, [state.user, state.products, authContext]);

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
            <FormControl id="Link Params">
                <FormLabel>Request Link Token</FormLabel>
                <Input key="linkInput" type="user" value={state.user} placeholder="John" onChange={updateUser}/>
                <FormHelperText>Username for Plaid.</FormHelperText>
            </FormControl>
            <Button colorScheme="teal"
                    size="md"
                    onClick={updateLinkToken}>
                Get Link Token
            </Button>
            {linkFlow}
        </div>
    )

}

export default LinkAccount;
