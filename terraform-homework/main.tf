terraform {                                                main.tf                                                            terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "2.16.0"
    }
  }
}

provider "docker" {
}

# Pulls the image
resource "docker_image" "nginx" {
  name         = "nginx:latest"
  keep_locally = true
}

# Create a container
resource "docker_container" "nginx-server" {
  image = docker_image.nginx.latest
  name  = "nginx-server"
  ports {
     internal = 80
  }

  volumes {
    container_path = "/usr/share/nginx/html"
    host_path      =  "/data/"
    read_only      =  true
  }

}
