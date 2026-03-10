resource "keycloak_realm" "realm" {
  realm                 = "javaland"
  enabled               = true
  access_token_lifespan = "2m"
}

resource "keycloak_openid_client" "api_client" {
  realm_id  = keycloak_realm.realm.id
  client_id = "api-client"

  name    = "API-Client"
  enabled = true

  access_type = "CONFIDENTIAL"

  service_accounts_enabled = true

  client_secret = random_password.api_client_javaland_01.result
}

resource "keycloak_openid_client_scope" "api_server_scope" {
  realm_id = keycloak_realm.realm.id
  name     = "api-server-scope"
}

resource "keycloak_openid_client_default_scopes" "client_default_scopes" {
  realm_id  = keycloak_realm.realm.id
  client_id = keycloak_openid_client.api_client.id

  default_scopes = [
  ]
}

resource "keycloak_openid_client_optional_scopes" "client_optional_scopes" {
  realm_id  = keycloak_realm.realm.id
  client_id = keycloak_openid_client.api_client.id

  optional_scopes = [
    keycloak_openid_client_scope.api_server_scope.name
  ]
}

resource "random_password" "api_client_javaland_01" {
  length  = 48
  special = false
}

resource "local_file" "client_secret_env" {
  content  = keycloak_openid_client.api_client.client_secret
  filename = "${path.module}/client_secret"
}

