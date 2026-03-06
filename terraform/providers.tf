terraform {
  required_providers {
    keycloak = {
      source  = "keycloak/keycloak"
      version = "5.7.0"
    }

    random = {
      source  = "hashicorp/random"
      version = "3.8.1"
    }

    local = {
      source  = "hashicorp/local"
      version = "2.7.0"
    }
  }
}

provider "keycloak" {
  client_id = "admin-cli"
  username  = "admin"
  password  = "admin"
  url       = "http://localhost:8081"
}
