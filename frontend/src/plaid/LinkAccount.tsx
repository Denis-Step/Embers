import React, {useState, useCallback, useMemo, useReducer} from 'react';
import { Button, FormControl, FormLabel, Input, FormHelperText } from "@chakra-ui/react"
import {PlaidLinkOnSuccessMetadata} from "react-plaid-link";
import {getLinkToken, requestItemCreation} from "../common/apicalls";
import LinkFlow from "./LinkFlow";
import {PlaidItemCreationRequest} from "../common/types";

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
    linkToken?: string;
    publicToken?: string;
    metaData?: PlaidLinkOnSuccessMetadata;
}

const initialState: State = {user: ""};

function linkReducer(state: State, action: Action): State {
    switch(action.type) {
        case ActionKind.UpdateUser:
            return { ...state, user: action.payload};
        case ActionKind.UpdateLinkToken:
            return {...state, linkToken: action.payload};
        case ActionKind.UpdatePublicToken:
            return {...state,
                publicToken: action.payload.publicToken,
                metaData: action.payload.metaData
            }
    }
}

const LinkAccount = () => {
    // State for getLinkToken params.
    const [state, dispatch] = useReducer(linkReducer, initialState);

    const updateUser = (event: React.FormEvent<HTMLInputElement>): void => {
        const input = event.currentTarget.value;
        dispatch({type: ActionKind.UpdateUser, payload: input});
    };

    // Handler for link token button.
    const updateLinkToken = useCallback(async () => {
        if (state.user) {
            const link = await getLinkToken(state.user);
            dispatch({type: ActionKind.UpdateLinkToken, payload: link});
        }
    }, [state.user]);

    // onSuccess callback for LinkFlow to initiate item creation
    // server-side.
    const onLinkSuccess = useCallback(async (public_token: string,
                                           metadata: PlaidLinkOnSuccessMetadata) => {
        dispatch({type: ActionKind.UpdatePublicToken,
            payload: {publicToken: public_token, metadata: metadata}});
        // @TODO: Send info back to server.
        console.log(metadata);
    },[])

    const linkFlow = useMemo(() => {
        // Fire off link flow iff there is a linkToken and no publicToken yet.
        if (state.linkToken && !Boolean(state.publicToken)) {
            return (<LinkFlow link_token={state.linkToken}
                      onSuccess={onLinkSuccess} />)
        } else {
            return null;
        }
        },
        [state.linkToken, state.publicToken, onLinkSuccess])


    return (
        <div id = "link-token-creation">
            <FormControl id="Link Params">
                <FormLabel>Request Link Token</FormLabel>
                <Input key="linkInput" type="user" value={state.user} placeholder="John" onChange={updateUser}  />
                <FormHelperText>Username for Plaid.</FormHelperText>
            </FormControl>
            <Button colorScheme="teal"
                    size="md"
                    onClick={updateLinkToken} >
                Get Link Token
            </Button>
            {linkFlow}
        </div>
    )

}

export default LinkAccount;
