import ch.admin.eid.did_sidekicks.DidDoc
import kotlin.test.Test
import kotlin.test.assertEquals

class PqeidAkpSmokeTest {
    @Test
    fun `getKeyByMethodId returns pub field for an AKP publicKeyJwk`() {
        val methodId = "did:webvh:QmZ5tnGo1fHNEzHDpG2Bx5dmT3eGNmBY9QATtm6DrFMzcH:example.com#auth-key-01"
        val didDocJson = """
            {
              "id": "did:webvh:QmZ5tnGo1fHNEzHDpG2Bx5dmT3eGNmBY9QATtm6DrFMzcH:example.com",
              "@context": ["https://www.w3.org/ns/did/v1"],
              "verificationMethod": [{
                "id": "$methodId",
                "controller": "did:webvh:QmZ5tnGo1fHNEzHDpG2Bx5dmT3eGNmBY9QATtm6DrFMzcH:example.com",
                "type": "JsonWebKey2020",
                "publicKeyJwk": {
                  "kty": "AKP",
                  "alg": "ML-DSA-44",
                  "kid": "auth-key-01",
                  "pub": "y0s5MPLAOhCEYcysEy35rbSAMs_C3wRGgiC-qwKGZSsEbEDBBrtD6No0awlidOH"
                }
              }]
            }
        """.trimIndent()

        val didDoc = DidDoc.fromJson(didDocJson)
        val jwk = didDoc.getKeyByMethodId(methodId)

        assertEquals("AKP", jwk.kty)
        assertEquals("ML-DSA-44", jwk.alg)
        assertEquals("y0s5MPLAOhCEYcysEy35rbSAMs_C3wRGgiC-qwKGZSsEbEDBBrtD6No0awlidOH", jwk.pubKey)
    }
}
