in progress:
- update examples
- refactor modules to be called sdk, rather than wpg
- attach javadoc to jar
- document logging / debugging etc
- amount -> need response / domain class, since credit/debit is only on result
- proxy support
- payment request API builder/converter to card payment request

long-term:
- make it so sessionctx doesnt need to be passed-in for threeds
- whether java 1.7 should be supported


descoped:
- payouts
- pay as order
- fast funds
- order inquiries (batch and non-batch)
    - bad practice
- mobile wallets
- industry data (airline data etc)
- pos integration / iso8583
- currency conversion
    - likely only payment pages, looks like direct auto-converts
- APMs (except paypal)
- overriding details for a token payment request
