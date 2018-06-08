import { Component, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

import * as $ from 'jquery';

@Component({
  selector: 'pay',
  templateUrl: 'pay.component.html',
  styles: []
})
export class PayComponent
{
    @ViewChild("response") responseBox;
    @ViewChild("paymentResponse") paymentResponseBox;

    loading: boolean = false;
    payType: string = "card";
    showCode: boolean = false;
    code: string = "java-sdk";
    data = null;

    public orderDetailsForm = this.fb.group({
        orderCode: ["order123", Validators.required],
        shopperId: ["shopper123", Validators.required],
        amount: ["1234", Validators.required],
        currency: ["GBP", Validators.required],
        description: ["Test order", Validators.required],
        address: ["123 Test Street", Validators.required],
        city: ["Cambridge", Validators.required],
        postalCode: ["orderDetailsForm", Validators.required],
        countryCode: ["GB", Validators.required]
    });

    constructor(
        public fb: FormBuilder
    ) {}

    toggleCode(codeElement)
    {
        console.log(codeElement);
        // Toggle state and scroll to code
        this.showCode = !this.showCode;
        $('html, body').animate({
            scrollTop: $(codeElement).offset().top
        }, 1000);
    }

    viewCode(code)
    {
        this.code = code;
    }

    showLoading()
    {
        this.loading = true;
        this.data = null;
    }

    handleResponse(data)
    {
        this.loading = false;
        this.data = data;

        // Resize box to fit content
        window.setTimeout(() => {
            var paymentResponseBox = this.paymentResponseBox.nativeElement;
            paymentResponseBox.style.height = "0px";
            paymentResponseBox.style.height = paymentResponseBox.scrollHeight + "px";
        }, 1);

        // Scroll to it
        $('html, body').animate({
            scrollTop: $(this.responseBox.nativeElement).offset().top
        }, 1000);
    }

    reset()
    {
        this.loading = false;
        this.data = null;
    }

    isFormDone()
    {
        return this.loading || this.data != null;
    }

}