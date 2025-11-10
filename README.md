# Documentación de la API de E-commerce

## Instrucciones de Ejecución con Docker

Para ejecutar la aplicación utilizando Docker, sigue los siguientes pasos:

1.  **Construir las imágenes de Docker:**
    Navega al directorio raíz del proyecto donde se encuentra el archivo `docker-compose.yaml` y ejecuta:
    ```bash
    docker-compose build
    ```

2.  **Iniciar los contenedores de Docker:**
    Una vez que las imágenes se hayan construido, puedes iniciar todos los servicios definidos en `docker-compose.yaml` con:
    ```bash
    docker-compose up
    ```
    Si deseas ejecutar los contenedores en segundo plano, usa:
    ```bash
    docker-compose up -d
    ```

3.  **Acceder a la aplicación:**
    La aplicación estará disponible en `http://localhost:8080` (o el puerto configurado en `application.yaml`).

4.  **Detener los contenedores de Docker:**
    Para detener los servicios y remover los contenedores, redes y volúmenes, ejecuta:
    ```bash
    docker-compose down
    ```

Este documento proporciona una descripción detallada de los endpoints de la API de E-commerce.

## Autenticación

Todos los endpoints requieren un Bearer Token en el encabezado `Authorization`.

`Authorization: Bearer <TU_JWT_TOKEN>`

## Endpoints para Clientes

### Autenticación

*   **POST** `/api/auth/register` - Registrar un nuevo usuario.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "email": "usuario@example.com",
            "name": "Usuario de Prueba",
            "password": "password123"
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "token": "eyJhbGciOiJIUzI1NiJ9...",
            "userId": 1,
            "email": "usuario@example.com",
            "name": "Usuario de Prueba",
            "role": "CLIENT"
        }
        ```

*   **POST** `/api/auth/login` - Iniciar sesión de un usuario.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "email": "usuario@example.com",
            "password": "password123"
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "token": "eyJhbGciOiJIUzI1NiJ9...",
            "userId": 1,
            "email": "usuario@example.com",
            "name": "Usuario de Prueba",
            "role": "CLIENT"
        }
        ```

### Perfil de Usuario

*   **GET** `/api/users/profile` - Obtener el perfil del usuario.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "email": "usuario@example.com",
            "name": "Usuario de Prueba",
            "role": "CLIENT",
            "status": "ACTIVE",
            "createdAt": "2025-11-09T10:00:00",
            "updatedAt": "2025-11-09T10:00:00"
        }
        ```

*   **PUT** `/api/users/profile` - Actualizar el perfil del usuario.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "name": "Nuevo Nombre"
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "email": "usuario@example.com",
            "name": "Nuevo Nombre",
            "role": "CLIENT",
            "status": "ACTIVE",
            "createdAt": "2025-11-09T10:00:00",
            "updatedAt": "2025-11-09T10:05:00"
        }
        ```

### Productos

*   **GET** `/api/products/` - Obtener todos los productos disponibles.
    *   **Respuesta Exitosa:**
        ```json
        {
            "products": [
                {
                    "id": 1,
                    "name": "Producto 1",
                    "description": "Descripción del producto 1",
                    "price": 19.99,
                    "stock": 100,
                    "createdAt": "2025-11-09T09:00:00",
                    "updatedAt": "2025-11-09T09:00:00"
                }
            ],
            "page": 0,
            "size": 20,
            "totalElements": 1,
            "totalPages": 1,
            "hasPrevious": false,
            "hasNext": false
        }
        ```

*   **GET** `/api/products/{id}` - Obtener un producto por su ID.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "name": "Producto 1",
            "description": "Descripción del producto 1",
            "price": 19.99,
            "stock": 100,
            "createdAt": "2025-11-09T09:00:00",
            "updatedAt": "2025-11-09T09:00:00"
        }
        ```

### Órdenes

*   **POST** `/api/orders` - Crear una nueva orden.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "items": [
                {
                    "productId": 1,
                    "quantity": 2
                }
            ],
            "shippingAddress": "Calle Principal 123"
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "orderNumber": "f82801e3-5920-4898-a7e8-5a29c5fea01c",
            "userId": 1,
            "status": "PENDING",
            "items": [
                {
                    "productId": 1,
                    "productName": "Producto 1",
                    "unitPrice": 19.99,
                    "quantity": 2,
                    "subtotal": 39.98
                }
            ],
            "totalAmount": 39.98,
            "shippingAddress": "Calle Principal 123",
            "createdAt": "2025-11-09T11:00:00",
            "updatedAt": "2025-11-09T11:00:00",
            "canBeCancelled": true
        }
        ```

*   **GET** `/api/orders` - Obtener todas las órdenes del usuario actual.
    *   **Respuesta Exitosa:**
        ```json
        {
            "orders": [
                {
                    "id": 1,
                    "orderNumber": "f82801e3-5920-4898-a7e8-5a29c5fea01c",
                    "userId": 1,
                    "status": "PENDING",
                    "items": [
                        {
                            "productId": 1,
                            "productName": "Producto 1",
                            "unitPrice": 19.99,
                            "quantity": 2,
                            "subtotal": 39.98
                        }
                    ],
                    "totalAmount": 39.98,
                    "shippingAddress": "Calle Principal 123",
                    "createdAt": "2025-11-09T11:00:00",
                    "updatedAt": "2025-11-09T11:00:00",
                    "canBeCancelled": true
                }
            ],
            "page": 0,
            "size": 20,
            "totalElements": 1,
            "totalPages": 1,
            "hasPrevious": false,
            "hasNext": false
        }
        ```

*   **GET** `/api/orders/{id}` - Obtener una orden por su ID.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "orderNumber": "f82801e3-5920-4898-a7e8-5a29c5fea01c",
            "userId": 1,
            "status": "PENDING",
            "items": [
                {
                    "productId": 1,
                    "productName": "Producto 1",
                    "unitPrice": 19.99,
                    "quantity": 2,
                    "subtotal": 39.98
                }
            ],
            "totalAmount": 39.98,
            "shippingAddress": "Calle Principal 123",
            "createdAt": "2025-11-09T11:00:00",
            "updatedAt": "2025-11-09T11:00:00",
            "canBeCancelled": true
        }
        ```

*   **GET** `/api/orders/number/{orderNumber}` - Obtener una orden por su número de orden.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "orderNumber": "f82801e3-5920-4898-a7e8-5a29c5fea01c",
            "userId": 1,
            "status": "PENDING",
            "items": [
                {
                    "productId": 1,
                    "productName": "Producto 1",
                    "unitPrice": 19.99,
                    "quantity": 2,
                    "subtotal": 39.98
                }
            ],
            "totalAmount": 39.98,
            "shippingAddress": "Calle Principal 123",
            "createdAt": "2025-11-09T11:00:00",
            "updatedAt": "2025-11-09T11:00:00",
            "canBeCancelled": true
        }
        ```

*   **POST** `/api/orders/{id}/cancel` - Cancelar una orden.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "orderNumber": "f82801e3-5920-4898-a7e8-5a29c5fea01c",
            "userId": 1,
            "status": "CANCELLED",
            "items": [
                {
                    "productId": 1,
                    "productName": "Producto 1",
                    "unitPrice": 19.99,
                    "quantity": 2,
                    "subtotal": 39.98
                }
            ],
            "totalAmount": 39.98,
            "shippingAddress": "Calle Principal 123",
            "createdAt": "2025-11-09T11:00:00",
            "updatedAt": "2025-11-09T11:05:00",
            "canBeCancelled": false
        }
        ```

## Endpoints para Administradores

### Gestión de Usuarios

*   **GET** `/api/admin/users` - Obtener todos los usuarios.
    *   **Respuesta Exitosa:**
        ```json
        {
            "users": [
                {
                    "id": 1,
                    "email": "usuario@example.com",
                    "name": "Usuario de Prueba",
                    "role": "CLIENT",
                    "status": "ACTIVE",
                    "createdAt": "2025-11-09T10:00:00",
                    "updatedAt": "2025-11-09T10:00:00"
                }
            ],
            "page": 0,
            "size": 20,
            "totalElements": 1,
            "totalPages": 1,
            "hasPrevious": false,
            "hasNext": false
        }
        ```

*   **GET** `/api/admin/users/{id}` - Obtener un usuario por su ID.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "email": "usuario@example.com",
            "name": "Usuario de Prueba",
            "role": "CLIENT",
            "status": "ACTIVE",
            "createdAt": "2025-11-09T10:00:00",
            "updatedAt": "2025-11-09T10:00:00"
        }
        ```

*   **POST** `/api/admin/users/{id}/deactivate` - Desactivar un usuario.
    *   **Respuesta Exitosa:** `204 No Content`

*   **POST** `/api/admin/users/{id}/activate` - Activar un usuario.
    *   **Respuesta Exitosa:** `204 No Content`

### Gestión de Productos

*   **GET** `/api/admin/products` - Obtener todos los productos.
    *   **Respuesta Exitosa:**
        ```json
        {
            "products": [
                {
                    "id": 1,
                    "name": "Producto 1",
                    "description": "Descripción del producto 1",
                    "price": 19.99,
                    "stock": 100,
                    "createdAt": "2025-11-09T09:00:00",
                    "updatedAt": "2025-11-09T09:00:00"
                }
            ],
            "page": 0,
            "size": 20,
            "totalElements": 1,
            "totalPages": 1,
            "hasPrevious": false,
            "hasNext": false
        }
        ```

*   **GET** `/api/admin/products/{id}` - Obtener un producto por su ID.
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "name": "Producto 1",
            "description": "Descripción del producto 1",
            "price": 19.99,
            "stock": 100,
            "createdAt": "2025-11-09T09:00:00",
            "updatedAt": "2025-11-09T09:00:00"
        }
        ```

*   **POST** `/api/admin/products` - Crear un nuevo producto.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "name": "Nuevo Producto",
            "description": "Descripción del producto",
            "price": 19.99,
            "stock": 100
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 2,
            "name": "Nuevo Producto",
            "description": "Descripción del producto",
            "price": 19.99,
            "stock": 100,
            "createdAt": "2025-11-09T12:00:00",
            "updatedAt": "2025-11-09T12:00:00"
        }
        ```

*   **PUT** `/api/admin/products/{id}` - Actualizar un producto.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "name": "Nombre del Producto Actualizado",
            "description": "Descripción actualizada",
            "price": 29.99
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "name": "Nombre del Producto Actualizado",
            "description": "Descripción actualizada",
            "price": 29.99,
            "stock": 100,
            "createdAt": "2025-11-09T09:00:00",
            "updatedAt": "2025-11-09T12:05:00"
        }
        ```

*   **DELETE** `/api/admin/products/{id}` - Eliminar un producto.
    *   **Respuesta Exitosa:** `204 No Content`

*   **POST** `/api/admin/products/{id}/reduce-stock` - Reducir el stock de un producto.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "quantity": 10
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "name": "Producto 1",
            "description": "Descripción del producto 1",
            "price": 19.99,
            "stock": 90,
            "createdAt": "2025-11-09T09:00:00",
            "updatedAt": "2025-11-09T12:10:00"
        }
        ```

*   **POST** `/api/admin/products/{id}/increase-stock` - Aumentar el stock de un producto.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "quantity": 10
        }
        ```
    *   **Respuesta Exitosa:**
        ```json
        {
            "id": 1,
            "name": "Producto 1",
            "description": "Descripción del producto 1",
            "price": 19.99,
            "stock": 100,
            "createdAt": "2025-11-09T09:00:00",
            "updatedAt": "2025-11-09T12:15:00"
        }
        ```

### Gestión de Órdenes

*   **GET** `/api/admin/orders` - Obtener todas las órdenes.
    *   **Respuesta Exitosa:**
        ```json
        {
            "orders": [
                {
                    "id": 1,
                    "orderNumber": "f82801e3-5920-4898-a7e8-5a29c5fea01c",
                    "userId": 1,
                    "status": "PENDING",
                    "items": [
                        {
                            "productId": 1,
                            "productName": "Producto 1",
                            "unitPrice": 19.99,
                            "quantity": 2,
                            "subtotal": 39.98
                        }
                    ],
                    "totalAmount": 39.98,
                    "shippingAddress": "Calle Principal 123",
                    "createdAt": "2025-11-09T11:00:00",
                    "updatedAt": "2025-11-09T11:00:00",
                    "canBeCancelled": true
                }
            ],
            "page": 0,
            "size": 50,
            "totalElements": 1,
            "totalPages": 1,
            "hasPrevious": false,
            "hasNext": false
        }
        ```

*   **GET** `/api/admin/orders/status/{status}` - Obtener órdenes por estado.
    *   **Respuesta Exitosa:** (Similar a `/api/admin/orders`)

*   **GET** `/api/admin/orders/recent` - Obtener órdenes recientes.
    *   **Respuesta Exitosa:** (Similar a `/api/admin/orders`)

*   **POST** `/api/admin/orders/{id}/confirm` - Confirmar una orden.
    *   **Respuesta Exitosa:** (Similar a la respuesta de una orden, con `status` "CONFIRMED")

*   **POST** `/api/admin/orders/{id}/process` - Procesar una orden.
    *   **Respuesta Exitosa:** (Similar a la respuesta de una orden, con `status` "PROCESSING")

*   **POST** `/api/admin/orders/{id}/ship` - Enviar una orden.
    *   **Respuesta Exitosa:** (Similar a la respuesta de una orden, con `status` "SHIPPED")

*   **POST** `/api/admin/orders/{id}/deliver` - Entregar una orden.
    *   **Respuesta Exitosa:** (Similar a la respuesta de una orden, con `status` "DELIVERED")

*   **GET** `/api/admin/orders/user/{userId}` - Obtener todas las órdenes de un usuario específico.
    *   **Respuesta Exitosa:** (Similar a `/api/admin/orders`)

*   **PUT** `/api/admin/orders/{id}/shipping-address` - Actualizar la dirección de envío de una orden.
    *   **Cuerpo de la solicitud:**
        ```json
        {
            "newShippingAddress": "Nueva Dirección 456"
        }
        ```
    *   **Respuesta Exitosa:** (Similar a la respuesta de una orden, con `shippingAddress` actualizado)

*   **POST** `/api/admin/orders/{id}/refund` - Reembolsar una orden.
    *   **Respuesta Exitosa:** (Similar a la respuesta de una orden, con `status` "CANCELLED")

*   **GET** `/api/admin/orders/statistics` - Obtener estadísticas de las órdenes.
    *   **Respuesta Exitosa:**
        ```json
        {
            "totalOrders": 100,
            "pendingOrders": 10,
            "confirmedOrders": 20,
            "processingOrders": 15,
            "shippedOrders": 25,
            "deliveredOrders": 30,
            "cancelledOrders": 0,
            "totalRevenue": 5000.00,
            "averageOrderValue": 50.00,
            "ordersLast7Days": 20
        }
        ```

*   **GET** `/api/admin/orders/status-counts` - Obtener el recuento de órdenes por estado.
    *   **Respuesta Exitosa:**
        ```json
        [
            {
                "status": "PENDING",
                "count": 10
            },
            {
                "status": "DELIVERED",
                "count": 30
            }
        ]
        ```

