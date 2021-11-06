import React, {useState, useCallback} from 'react';
import {PlaidLinkOnSuccessMetadata, PlaidLinkOptions, usePlaidLink} from "react-plaid-link";

export interface LinkFlowProps {
    link_token: string,
    // Callback
    onSuccess: (public_token: string, metadata: PlaidLinkOnSuccessMetadata) => Promise<any>;

}

const LinkFlow = (props: LinkFlowProps) => {

    const config: PlaidLinkOptions = {
        onSuccess: props.onSuccess,
        onExit: (err, metadata) => {
            console.log("error", err);
            console.log("metadata", metadata);
        },
        token: props.link_token,
        // Required for OAuth:
        // receivedRedirectUri: window.location.href,
    }

    const {open} = usePlaidLink(config);

    // Will imperatively fire the Plaid Link components.
    // Plaid Link will close itself when complete.
    open();
    return null
}

export default LinkFlow;
