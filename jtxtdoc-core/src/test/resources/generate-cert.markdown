# Certificats SSL et Java  

Ce guide permet de dÃ©crire les Ã©tapes nÃ©cessaires Ã  la mise en place d&#8217;un certificat SSL dans un environnement Java

## Ressources nÃ©cessaires

Ce guide repose sur le tÃ©lÃ©chargement prÃ©alable des outils suivants :

1. Open SSL [TÃ©lÃ©chargement Open SSL](http://gnuwin32.sourceforge.net/packages/openssl.htm) 
2. un JDK (ici JDK8)
## Utilisation d&#8217;OpenSSL (Windows)

Il faut tout d&#8217;abord dÃ©zipper OpenSSL. Il faut se placer dans un rÃ©pertoire facile Ã  accÃ©der, par exemple `D:\OpenSSL` pour lancer l&#8217;extraction.

Les opÃ©rations suivantes se font en ligne de commande. Ouvrir une console **en mode adminstrateur**, puis se positionner sur `D:\OpenSSL`.  
Vous aurez besoin Ã©galement d&#8217;un fichier &#171;openssl.cnf&#187;, qui n&#8217;est pas toujours fourni. [openssl.cnf]("openssl.cnf") 

    D:
    cd OpenSSL\bin
    set OPENSSL_CONF=D:\OpenSSL\openssl.cnf 
    openssl.exe

Le prompt OpenSSL s&#8217;affiche, lancez la commande suivante pour lancer la gÃ©nÃ©ration de la clÃ© serveur

## Utilisation d&#8217;OpenSSL (Linux)

Dans la plupart des cas, Linux est fourni avec Open SSL, il est alors juste nÃ©cessaire d&#8217;exÃ©cuter

    sudo openssl

## GÃ©nÃ©ration d&#8217;un certificat autosignÃ© (version globale)

Cette mÃ©thode est intÃ©ressante si vous avez besoin de rÃ©utiliser le certificat dans d&#8217;autres contextes.

### gÃ©nÃ©ration de la clÃ© serveur

Il faut dans un premier temps gÃ©nÃ©rer la clÃ© privÃ©e du serveur, entrer la commande suivante:

    openssl genrsa -out server.key 2048

Cela gÃ©nÃ¨re la clÃ© serveur server.key, contrÃ´ler sa bonne crÃ©ation dans le rÃ©pertoire courant

### gÃ©nÃ©ration du certificat

EnchaÃ®ner les deux commandes suivantes, qui permettent de dÃ©clarer une requÃªte d&#8217;obtention de certificat, puis de valider cette demande

    req -new -key server.key -out server.csr 
    x509 -req -days 365 -in server.csr -signkey server.key -out server.crt

Votre certificat est gÃ©nÃ©rÃ© dans fichier server.crt

## GÃ©nÃ©ration d&#8217;un certificat autosignÃ© et intÃ©gration automatique dans un JKS

## IntÃ©gration avec Java

Il va falloir intÃ©grer le certificat gÃ©nÃ©rÃ© dans le Java Key Store (JKS).

