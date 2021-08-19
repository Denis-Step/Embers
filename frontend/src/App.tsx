import React from 'react';
import LinkAccount from "./plaid/LinkAccount";
import { ChakraProvider } from "@chakra-ui/react"

function App() {
  return (
      <ChakraProvider>
        <div className="App">
          <LinkAccount />
        </div>
      </ChakraProvider>
  );
}

export default App;
