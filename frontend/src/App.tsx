import React from 'react';
import LinkAccount from "./plaid/LinkAccount";
import { ChakraProvider } from "@chakra-ui/react"
import {JPGoogleLogin} from "./auth/JPGoogleLogin";

function App() {
  return (
      <ChakraProvider>
        <div className="App">
          <LinkAccount />
          <JPGoogleLogin />
        </div>
      </ChakraProvider>
  );
}

export default App;
