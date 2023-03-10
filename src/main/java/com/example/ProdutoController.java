package com.example;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProdutoController {

    private static final String SESSION_CARRINHO = "sessionCarrinho";
    @Autowired
    ProdutoRepository produtoRepository;


    @GetMapping("/novo-produto")
    public String mostrarFormNovoProduto(Produto produto){

        return "novo-produto";
    }


    @GetMapping(value={"/index", "/"})
    public String mostrarListaProdutos(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "index";
    }


    @PostMapping("/adicionar-produto")
    public String adicionarProduto(@Valid Produto produto, BindingResult result) {
        if (result.hasErrors()) {
            return "/novo-produto";
        }

        produtoRepository.save(produto);
        return "redirect:/index";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormAtualizar(@PathVariable("id") int id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "O id do produto é inválido:" + id));


        model.addAttribute("produto", produto);
        return "atualizar-produto";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarProduto(@PathVariable("id") int id, @Valid Produto produto,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            produto.setId(id);
            return "atualizar-produto";
        }


        produtoRepository.save(produto);
        return "redirect:/index";
    }

    @GetMapping("/remover/{id}")
    public String removerProduto(@PathVariable("id") int id, HttpServletRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("O id do produto é inválido:" + id));
        produtoRepository.delete(produto);


        List<Produto> sessionCarrinho =
                (List<Produto>) request.getSession().getAttribute(SESSION_CARRINHO);


        sessionCarrinho.remove(produto);


        return "redirect:/index";
    }

    @GetMapping("/carrinho")
    public String mostrarCarrinho(Model model, HttpServletRequest request){
        List<Produto> carrinho =
                (List<Produto>) request.getSession().getAttribute(SESSION_CARRINHO);
        model.addAttribute("sessionCarrinho",
                !CollectionUtils.isEmpty(carrinho) ? carrinho : new ArrayList<>());

        return "carrinho";
    }

    @GetMapping("/carrinho/remover/{id}")
    public String removerDoCarrinho(@PathVariable("id") int id, HttpServletRequest request) {


        List<ProdutosCarrinho> sessionCarrinho =
                (List<ProdutosCarrinho>) request.getSession().getAttribute(SESSION_CARRINHO);


        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(
                        "O id do produto é inválido: " + id));


        sessionCarrinho.remove(produto);


        return "redirect:/carrinho";
    }

    @GetMapping("/adicionarCarrinho/{id}")
    public String adicionarAoCarrinho (@PathVariable("id") int id, HttpServletRequest request) {

        Produto produto = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("O id do produto é inválido: " + id));

        List<ProdutosCarrinho> carrinho = (List<ProdutosCarrinho>)request.getSession().getAttribute(SESSION_CARRINHO);

        // se não tem nenhum produto adicionado ao carrinho ainda, cria um novo carrinho
        if (CollectionUtils.isEmpty(carrinho)) {
            carrinho = new ArrayList<ProdutosCarrinho>();
        }

        // se o carrinho já contem o produto, incrementa +1 na quantidade de produtos adicionados
        if (carrinho.contains(produto)) {

            // se quantidade disponivel > quantidade desejada
            if (produto.getQuantidade() > carrinho.getQuantidadeCarrinho()) {
                carrinho.setQuantidadeCarrinho(1);
            }

        } else {
            carrinho.add((ProdutosCarrinho) produto);
        }

        request.getSession().setAttribute(SESSION_CARRINHO, carrinho);

        return "redirect:/carrinho";

    }

}
