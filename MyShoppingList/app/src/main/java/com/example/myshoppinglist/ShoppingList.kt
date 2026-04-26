
package com.example.myshoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShoppingItem(val id:Int,
                        var name: String,
                        var quantity: Int,
                        var isEditing: Boolean=false
)

@Composable
fun ShoppingListApp(innerPadding: PaddingValues) {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text("Add Item",
//                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
//                modifier = Modifier.size(18.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(sItems) {
                item ->
                if(item.isEditing){
                    ShoppingItemEditor(item = item, onEditComplete = {
                        editedName,editedQuantity->
                        sItems= sItems.map{it.copy(isEditing= false)}
                        val editedItem= sItems.find{ it.id == item.id }
                        editedItem?.let {
                            it.name= editedName
                            it.quantity= editedQuantity
                        }
                    })
                }else{
                    //finding which item we are editing and changing is "isEditing Boolean" to true
                    ShoppingListItem(item= item ,
                        onEditClick= {
                            sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                                     },
                        onDeleteClick = {
                            sItems=sItems - item
                        })
                }


            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if(itemName.isNotBlank()){
                            val newItem= ShoppingItem(
                                id =sItems.size+1 ,
                                name =itemName,
                                quantity = itemQuantity.toIntOrNull() ?: 1
                            )
                            sItems+=newItem
                            showDialog= false
                            itemName= ""
                            itemQuantity=""
                        }
                    }) {
                        Text("Add",
//                            fontSize = 18.sp
                        )
                    }
                    Button(onClick = {showDialog=false}) {
                        Text("Cancel",
//                            fontSize = 18.sp
                        )
                    }
                }
            },
                title = { Text("Add Shopping Item", fontSize = 24.sp) },
                text =  {
                    Column {
                        OutlinedTextField(value =itemName,
                            onValueChange ={itemName= it},
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
//                            textStyle = TextStyle(fontSize = 18.sp)

                        )
                        OutlinedTextField(value = itemQuantity,
                            onValueChange ={itemQuantity= it},
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
//                            textStyle = TextStyle(fontSize = 18.sp)
                        )
                    }
                }

            )
        }
    }
@Composable
fun ShoppingItemEditor(item: ShoppingItem,onEditComplete:(String,Int)->Unit) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row(modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            BasicTextField(
                value = editedName,
                onValueChange = { editedName = it },
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp),
//                textStyle = TextStyle(fontSize = 18.sp)
            )
            BasicTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                singleLine = true,
                modifier = Modifier.wrapContentSize().padding(8.dp),
//                textStyle = TextStyle(fontSize = 18.sp)
            )
        }
        Button(
            onClick = {
                isEditing = false
                onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
            }
        ) {
            Text("Save",
//                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ShoppingListItem(   
    item: ShoppingItem,
    onEditClick: ()-> Unit,
    onDeleteClick: ()-> Unit,
) {
    Row(
        modifier = Modifier.padding(8.dp)
            .fillMaxWidth().border(
                border = BorderStroke(2.dp, Color(0xff018786)),
                shape = RoundedCornerShape(20)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp), fontSize = 18.sp)
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp), fontSize = 18.sp)
        Row(modifier = Modifier
            .padding(8.dp)
        ) {
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Default.Edit,
                    contentDescription = null,
//                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = null,
//                    modifier = Modifier.size(18.dp)
                )
            }

        }
    }
}
