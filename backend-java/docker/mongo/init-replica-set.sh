#!/usr/bin/env bash
set -euo pipefail
until mongosh --host mongodb:27017 --quiet --eval "db.adminCommand('ping').ok" | grep -q 1; do
    echo "Esperando a MongoDB..."
    sleep 2
done

mongosh --host mongodb:27017 --quiet --eval '
try {
    const status = rs.status();
    if (status.ok === 1) {
        print("Replica set ya inicializado");
    }
} catch (error) {
    rs.initiate({
        _id: "rs0",
        members: [{ _id: 0, host: "mongodb:27017" }]
    });
}
'

until mongosh --host mongodb:27017 --quiet --eval "rs.status().myState" | grep -q 1; do
    echo "Esperando que el nodo MongoDB sea PRIMARY..."
    sleep 2
done

echo "Replica set rs0 listo"