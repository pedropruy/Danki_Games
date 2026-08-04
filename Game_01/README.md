# 🗡️ Top-Down Action RPG (Estilo *The Legend of Zelda*)

Um jogo de ação e aventura 2D que eu desenvolvi do zero em **Java**, focado em exploração, combate, sistemas de magia e mecânicas clássicas de RPG no estilo dos jogos top-down retrô.

---

## 🎮 Sobre o Projeto

Este projeto consiste na engine e mecânicas core de um RPG top-down. O jogo combina mecânicas clássicas de movimentação em 4 direções, sistema de câmera dinâmica, gerenciamento de áudio interno, persistência de dados via save/load com criptografia leve e tratamento otimizado de animações e renderização.

---

## 🚀 Principais Funcionalidades

### 🧙‍♂️ Gameplay & Mecânicas
* **Movimentação:** Movimentação em 4 direções, pulo e futuramente será implementado um dash.
* **Foco Mágico / Spells:** Suporte a habilidades mágicas e desbloqueio de livros de magias ao longo do jogo.
* **Movimentação A*:** Inimigos seguem uma movimentação com base no algoritmo A* que busca o caminho matematicamente mais curto.

### 🛠️ Arquitetura & Otimização
* **Renderização Eficiente de Sprites:** Processamento prévio de animações espelhadas economizando memória de GPU e tempo de execução no loop principa e renderização apenas do que é visto pela câmera interna.
* **Câmera Dinâmica:** Algoritmo de acompanhamento suave focado no jogador.
* **Interface Dinâmica (UI):** Renderização de textos e elementos de interface perfeitamente centralizados na tela.

### 💾 Persistência de Dados
* **Save / Load System:** Algoritmo próprio de codificação/decodificação para persistência de estado do jogador (posição, equipamentos, livros de magia coletados e progresso).

---

#### 🎨 Recursos Visuais e Sonoros

* **Áudio e Trilha Sonora:** Todos os efeitos sonoros (SFX) e composições musicais foram criados do zero por mim utilizando a ferramenta [BeepBox](https://www.beepbox.co/).
* **Interface e Efeitos (UI & Spells):** Todos os elementos de interface de usuário (UI), livros de magia e os efeitos visuais das magias foram desenhados por mim.
* **Sprites de Cenário e Personagem:** Os sprites de mapa e do personagem principal foram adaptados do jogo original *Final Fantasy I* (Square Enix) e são utilizados aqui exclusivamente para **fins educacionais e portfólio pessoal sem fins lucrativos**.

---

## ⚖️ Isenção de Responsabilidade (Copyright Disclaimer)

Este é um projeto **não comercial e de código aberto**, desenvolvido estritamente para fins de aprendizado, estudo de arquitetura de jogos em Java e demonstração de portfólio. 

* Os direitos de marca, sprites e artes originais de *Final Fantasy I* pertencem à **Square Enix Co., Ltd.**.
* O uso dessas imagens se enquadra como *Fair Use* (uso justo) para fins educacionais e demonstrativos.