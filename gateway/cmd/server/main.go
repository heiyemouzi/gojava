package main

import (
	"flag"
	"fmt"
	"log"

	"github.com/gojava/gateway/internal/config"
	"github.com/gojava/gateway/internal/router"
)

func main() {
	cfgPath := flag.String("f", "etc/gateway.yaml", "config file")
	flag.Parse()

	cfg, err := config.Load(*cfgPath)
	if err != nil {
		log.Fatalf("load config: %v", err)
	}
	if len(cfg.JWT.Secret) < 32 {
		log.Printf("WARN: jwt.secret length < 32, unsafe in production")
	}

	r := router.New(cfg)
	addr := fmt.Sprintf(":%d", cfg.Server.Port)
	log.Printf("gateway listening on %s", addr)
	if err := r.Run(addr); err != nil {
		log.Fatal(err)
	}
}
