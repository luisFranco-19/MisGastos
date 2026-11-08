# REGLAS DE DISEÑO Y DESIGN SKILLS — MIS GASTOS

## Mandato Obligatorio
Antes de modificar o crear cualquier interfaz, componente visual, pantalla, navegación visual, animación, botón, tarjeta, selector, diálogo, notificación o widget en **Mis Gastos**, es obligatorio respetar y aplicar las **DESIGN SKILLS** instaladas (`emil-design-eng`, `apple-design`, `animate`, `ask-sonner`, etc.).

---

## 1. Aplicación de Design Skills
1. Consultar las skills antes de cualquier cambio visual.
2. Extraer los principios aplicables (curvas de animación, tiempos < 300ms, escalas sutiles 0.95-0.97 en press, feedback táctil inmediato, interrupciones suaves, orígenes de transformación coherentes).
3. Aplicar antes de codificar la UI.
4. Mantener estricta consistencia visual con el resto de la app.

## 2. Preservación del Design System Existente
- **Reutilización:** Si existe un componente reutilizable (`AppComponents.kt`, `ExpenseCard.kt`, `Theme.kt`, etc.), USARLO. Prohibido duplicar componentes.
- **Tokens y Estilos:** Mantener la jerarquía tipográfica, espaciados (tokens de 4dp/8dp/16dp), radios de borde, elevaciones, colores semánticos (Light/Dark/System) y estados interactivos.

## 3. Coherencia en Nuevos Elementos
Todos los nuevos componentes (selectores de moneda, configuración de presupuestos, diálogos, switches, splash screen, widget, empty states, toasts/snackbars) deben sentirse nativos y concebidos como parte del mismo producto.

## 4. UX Antes de Codificar
- **Claridad > Decoración:** Flujos simplificados, reducción de pasos, jerarquía limpia, feedback visual instantáneo y soporte de accesibilidad.

## 5. Principios de Animación
- **Rápidas y con propósito:** Duraciones < 300ms (100-160ms para feedback de botones, 150-250ms para menús/selectores, 200-300ms para modales/sheets).
- **Easing:** `FastOutSlowInEasing` / `ease-out` para entradas; nunca `ease-in` en interacciones directas.
- **Entradas realistas:** Nunca animar desde `scale(0)` (comenzar desde `scale(0.95)` + `alpha: 0`).
- **No bloquear:** Las transiciones no deben bloquear la entrada del usuario.

## 6. Rendimiento Visual
- Evitar recomposiciones innecesarias en Compose (`derivedStateOf`, lambdas estables, Modifier con phase graphicsLayer).
- Evitar blurs pesados o procesos continuos en bucle.
- Rendimiento + UX + Diseño.

## 7. Widget
- Identidad visual compacta y legible.
- Botones de toque cómodo (mínimo 48dp de área de toque).
- Actualizaciones eficientes sin penalización de batería.

## 8. Checklist de Verificación Visual
- [ ] No hay elementos desalineados o clipping de texto.
- [ ] Áreas táctiles adecuadas (mínimo 48x48 dp).
- [ ] Espaciados y padding consistentes con el design system.
- [ ] Colores semánticos y contraste en temas Claro, Oscuro y Sistema.
- [ ] Widget consistente y con lectura instantánea.
- [ ] Animaciones sutiles, fluidas y de alto rendimiento.
- [ ] UI rápida, responsiva y sin sobrecostes de recomposición.
